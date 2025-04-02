package com.hsact.sunplanner.data

import com.hsact.sunplanner.network.OpenMeteoGeo
import com.hsact.sunplanner.network.OpenMeteoService

class WeatherRepository(private val service: OpenMeteoService, private val geolocationService: OpenMeteoGeo) {
    suspend fun getWeather(latitude: Double, longitude: Double, startDate: String, endDate: String) =
        service.getHistoricalWeather(latitude, longitude, startDate, endDate)

    suspend fun getCoordinatesByCity(cityName: String): Location? {
        return try {
            val response = geolocationService.getCityCoordinates(cityName)
            response.results?.firstOrNull()
        } catch (e: Exception) {
            println("Error fetching coordinates: ${e.message}")
            null
        }
    }
    suspend fun getCitiesList(cityName: String): List<Location>? {
        return try {
            val response = geolocationService.getCityCoordinates(cityName)
            response.results
        } catch (e: Exception) {
            println("Error fetching list of cities: ${e.message}")
            null
        }
    }
}