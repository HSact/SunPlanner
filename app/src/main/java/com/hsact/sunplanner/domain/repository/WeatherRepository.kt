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
     *
     * @param params The parameters defining the weather request (coordinates, dates, units, etc.).
     * @return The weather response data.
     * @throws Exception if the network request or data parsing fails.
     */
    suspend fun getWeather(
        params: WeatherRequestParams
    ): WeatherResponse

    /**
     * Retrieves geographic coordinates for a given city name.
     *
     * @param cityName The name of the city to search coordinates for.
     * @return The [Location] object with coordinates if found, or null otherwise.
     */
    suspend fun getCoordinatesByCity(cityName: String): Location?

    /**
     * Retrieves a list of cities matching the given city name and language.
     *
     * @param cityName The name of the city to search for.
     * @param language The language code for localized city names.
     * @return A list of [Location] objects matching the search, or null if none found.
     */
    suspend fun getCitiesList(cityName: String, language: String): List<Location>?
}