package com.hsact.sunplanner.data.repository

import com.hsact.sunplanner.data.network.OpenMeteoGeo
import com.hsact.sunplanner.data.network.OpenMeteoService
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: OpenMeteoService,
    private val geolocationService: OpenMeteoGeo
) : WeatherRepository {

    override suspend fun getWeather(params: WeatherRequestParams) =
        service.getHistoricalWeather(
            latitude = params.latitude,
            longitude = params.longitude,
            startDate = params.startDate,
            endDate = params.endDate,
            temperatureUnit = params.temperatureUnit,
            windSpeedUnit = params.windSpeedUnit,
            precipitationUnit = params.precipitationUnit
        )

    override suspend fun getCoordinatesByCity(cityName: String): Location? {
        return try {
            val response = geolocationService.getCityCoordinates(cityName)
            response.results?.firstOrNull()
        } catch (e: Exception) {
            throw Exception("Error fetching coordinates: ${e.message}")
        }
    }

    override suspend fun getCitiesList(cityName: String, language: String): List<Location>? {
        return try {
            val response = geolocationService.getCityCoordinates(cityName, language = language)
            response.results
        } catch (e: Exception) {
            throw Exception("Error fetching cities: ${e.message}")
        }
    }
}