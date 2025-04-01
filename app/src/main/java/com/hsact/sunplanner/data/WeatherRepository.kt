package com.hsact.sunplanner.data

import com.hsact.sunplanner.network.OpenMeteoService

class WeatherRepository(private val service: OpenMeteoService) {
    suspend fun getWeather(latitude: Double, longitude: Double, startDate: String, endDate: String) =
        service.getHistoricalWeather(latitude, longitude, startDate, endDate)
}