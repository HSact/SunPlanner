package com.hsact.sunplanner.data.repository

import android.util.Log
import com.hsact.sunplanner.data.local.db.CachedWeather
import com.hsact.sunplanner.data.local.db.WeatherCacheDao
import com.hsact.sunplanner.data.network.AirQualityResponse
import com.hsact.sunplanner.data.network.OpenMeteoAirQuality
import com.hsact.sunplanner.data.network.OpenMeteoGeo
import com.hsact.sunplanner.data.network.OpenMeteoService
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.di.DefaultDispatcher
import com.hsact.sunplanner.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val openMeteoService: OpenMeteoService,
    private val openMeteoGeo: OpenMeteoGeo,
    private val airQualityService: OpenMeteoAirQuality,
    private val cacheDao: WeatherCacheDao,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun getCitiesList(cityName: String, language: String): List<Location>? {
        return try {
            val response = openMeteoGeo.getCityCoordinates(cityName, 20, language)
            response.results
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Error fetching city list", e)
            null
        }
    }

    override suspend fun getCoordinatesByCity(cityName: String): Location? {
        return try {
            val response = openMeteoGeo.getCityCoordinates(cityName, 1)
            response.results?.firstOrNull()
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Error fetching coordinates by city", e)
            null
        }
    }

    /**
     * Fetches historical weather and air quality data, combining them into a single [WeatherResponse].
     * 
     * Features:
     * - Multi-source data: Weather (ERA5) and Air Quality (CAMS).
     * - Intelligent AQI processing: Hourly air quality data is grouped and averaged per day 
     *   by matching dates, ensuring accuracy across time zones.
     * - Caching: Responses are saved in a local DB to minimize network usage.
     * - Automatic cache cleanup of old entries.
     */
    override suspend fun getWeather(params: WeatherRequestParams): WeatherResponse {
        val cacheId = generateCacheId(params)
        Log.d("SunPlannerDebug", "getWeather started. CacheId: $cacheId")

        val cached = cacheDao.getCachedWeather(cacheId)
        if (cached != null) {
            Log.d("SunPlannerDebug", "Found exact data match in cache")
            try {
                return withContext(defaultDispatcher) {
                    Json.decodeFromString<WeatherResponse>(cached.jsonResponse)
                }
            } catch (e: Exception) {
                Log.e("SunPlannerDebug", "Error decoding cache", e)
            }
        }

        // Search for a candidate cached entry for the location that covers the requested date range
        try {
            val candidates = cacheDao.getCachedWeatherForLocation(
                latitude = params.latitude,
                longitude = params.longitude,
                tempUnit = params.temperatureUnit,
                windUnit = params.windSpeedUnit,
                precipUnit = params.precipitationUnit
            )
            if (candidates.isNotEmpty()) {
                val reqStart = LocalDate.parse(params.startDate)
                val reqEnd = LocalDate.parse(params.endDate)

                val covering = candidates.find { candidate ->
                    try {
                        val cachedStart = LocalDate.parse(candidate.startDate)
                        val cachedEnd = LocalDate.parse(candidate.endDate)
                        isDateRangeCovered(reqStart, reqEnd, cachedStart, cachedEnd)
                    } catch (_: Exception) {
                        false
                    }
                }

                if (covering != null) {
                    Log.d("SunPlannerDebug", "Found covering cache entry: ${covering.id}")
                    return withContext(defaultDispatcher) {
                        Json.decodeFromString<WeatherResponse>(covering.jsonResponse)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SunPlannerDebug", "Error checking covering cache entries", e)
        }

        return coroutineScope {
            Log.d("SunPlannerDebug", "Fetching from network...")
            val weatherDeferred = async {
                openMeteoService.getHistoricalWeather(
                    latitude = params.latitude,
                    longitude = params.longitude,
                    startDate = params.startDate,
                    endDate = params.endDate,
                    daily = params.daily,
                    temperatureUnit = params.temperatureUnit,
                    windSpeedUnit = params.windSpeedUnit,
                    precipitationUnit = params.precipitationUnit,
                    timezone = params.timezone
                )
            }

            val aqiDeferred = async {
                try {
                    airQualityService.getAirQuality(
                        latitude = params.latitude, longitude = params.longitude,
                        startDate = params.startDate, endDate = params.endDate
                    )
                } catch (e: Exception) {
                    Log.e("SunPlannerDebug", "AQI Fetch failed", e)
                    null
                }
            }

            val weatherResponse = weatherDeferred.await()
            Log.d("SunPlannerDebug", "Weather network response received")
            val aqiResponse = aqiDeferred.await()
            Log.d("SunPlannerDebug", "AQI network response: ${aqiResponse != null}")

            val dailyAqi = withContext(defaultDispatcher) {
                aggregateHourlyAqiToDaily(aqiResponse, weatherResponse.daily.time)
            }
            Log.d("SunPlannerDebug", "Processed AQI size: ${dailyAqi.size}")

            val finalResponse = weatherResponse.copy(
                daily = weatherResponse.daily.copy(european_aqi = dailyAqi)
            )

            try {
                val json = withContext(defaultDispatcher) {
                    Json.encodeToString(finalResponse)
                }
                cacheDao.insertWeather(
                    CachedWeather(
                        id = cacheId,
                        latitude = params.latitude,
                        longitude = params.longitude,
                        startDate = params.startDate,
                        endDate = params.endDate,
                        tempUnit = params.temperatureUnit,
                        windUnit = params.windSpeedUnit,
                        precipUnit = params.precipitationUnit,
                        jsonResponse = json
                    )
                )
                cacheDao.clearOldCache()
                Log.d("SunPlannerDebug", "Response saved to cache")
            } catch (e: Exception) {
                Log.e("SunPlannerDebug", "Error saving to cache", e)
            }

            finalResponse
        }
    }

    private fun isDateRangeCovered(
        reqStart: LocalDate,
        reqEnd: LocalDate,
        cachedStart: LocalDate,
        cachedEnd: LocalDate
    ): Boolean {
        val reqStartMD = reqStart.monthValue * 100 + reqStart.dayOfMonth
        val reqEndMD = reqEnd.monthValue * 100 + reqEnd.dayOfMonth
        val cachedStartMD = cachedStart.monthValue * 100 + cachedStart.dayOfMonth
        val cachedEndMD = cachedEnd.monthValue * 100 + cachedEnd.dayOfMonth

        val reqWraps = reqStartMD > reqEndMD
        val cachedWraps = cachedStartMD > cachedEndMD

        return when {
            !reqWraps && !cachedWraps -> {
                reqStartMD >= cachedStartMD && reqEndMD <= cachedEndMD
            }
            reqWraps && cachedWraps -> {
                reqStartMD >= cachedStartMD && reqEndMD <= cachedEndMD
            }
            !reqWraps && cachedWraps -> {
                reqStartMD >= cachedStartMD || reqEndMD <= cachedEndMD
            }
            else -> false
        }
    }

    override suspend fun clearCache() {
        cacheDao.clearAll()
    }

    private fun generateCacheId(params: WeatherRequestParams): String {
        return "${params.latitude}_${params.longitude}_${params.startDate}_${params.endDate}_${params.temperatureUnit}_${params.windSpeedUnit}_${params.precipitationUnit}"
    }

    /**
     * Aggregates hourly AQI data into daily averages based on the dates provided in [targetDays].
     */
    private fun aggregateHourlyAqiToDaily(
        aqiResponse: AirQualityResponse?,
        targetDays: List<String>
    ): List<Double?> {
        if (aqiResponse == null) return emptyList()

        val hourly = aqiResponse.hourly
        val timeMap = mutableMapOf<String, MutableList<Double>>()

        hourly.time.forEachIndexed { index, timeStr ->
            val date = timeStr.substring(0, 10) // Extracts yyyy-MM-dd
            val value = hourly.european_aqi.getOrNull(index)
            if (value != null) {
                timeMap.getOrPut(date) { mutableListOf() }.add(value)
            }
        }

        return targetDays.map { day ->
            timeMap[day]?.average()?.takeIf { !it.isNaN() }
        }
    }
}
