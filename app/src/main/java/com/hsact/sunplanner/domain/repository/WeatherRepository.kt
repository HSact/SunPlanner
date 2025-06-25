package com.hsact.sunplanner.domain.repository

import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse

interface WeatherRepository {
    suspend fun getWeather(
        params: WeatherRequestParams
    ): WeatherResponse

    suspend fun getCoordinatesByCity(cityName: String): Location?
    suspend fun getCitiesList(cityName: String, language: String): List<Location>?
}