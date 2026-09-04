package com.hsact.sunplanner.domain.repository

import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse

/**
 * Repository interface for fetching weather data and location information.
 */
interface WeatherRepository {

    /**
     * Fetches historical weather data based on the provided request parameters.
     */
    suspend fun getWeather(params: WeatherRequestParams): WeatherResponse

    /**
     * Retrieves geographic coordinates for a given city name.
     */
    suspend fun getCoordinatesByCity(cityName: String): Location?

    /**
     * Retrieves a list of cities matching the given city name and language.
     */
    suspend fun getCitiesList(cityName: String, language: String): List<Location>?

    /**
     * Clears all cached weather data.
     */
    suspend fun clearCache()
}
