package com.hsact.sunplanner.data.repository

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse

interface WeatherRepository {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String,
        temperatureUnit: String,
        windSpeedUnit: String,
        precipitationUnit: String
    ): WeatherResponse

    suspend fun getCoordinatesByCity(cityName: String): Location?
    suspend fun getCitiesList(cityName: String, language: String): List<Location>?
}