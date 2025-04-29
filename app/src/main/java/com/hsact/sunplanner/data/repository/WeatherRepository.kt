package com.hsact.sunplanner.data.repository

import com.hsact.sunplanner.data.network.OpenMeteoGeo
import com.hsact.sunplanner.data.network.OpenMeteoService
import com.hsact.sunplanner.data.responses.Location
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val service: OpenMeteoService, private val geolocationService: OpenMeteoGeo
) {

    suspend fun getWeather(
        latitude: Double, longitude: Double,
        startDate: String, endDate: String,
        temperatureUnit: String,
        windSpeedUnit: String,
        precipitationUnit: String
    ) =
        service.getHistoricalWeather(
            latitude,
            longitude,
            startDate,
            endDate,
            temperatureUnit = temperatureUnit,
            windSpeedUnit = windSpeedUnit,
            precipitationUnit = precipitationUnit
        )

    suspend fun getCoordinatesByCity(cityName: String): Location? {
        return try {
            val response = geolocationService.getCityCoordinates(cityName)
            response.results?.firstOrNull()
        } catch (e: Exception) {
            println("Error fetching coordinates: ${e.message}")
            null
        }
    }

    suspend fun getCitiesList(cityName: String, language: String): List<Location>? {
        return try {
            val response = geolocationService.getCityCoordinates(cityName, language = language)
            response.results
        } catch (e: Exception) {
            println("Error fetching list of cities: ${e.message}")
            null
        }
    }
}