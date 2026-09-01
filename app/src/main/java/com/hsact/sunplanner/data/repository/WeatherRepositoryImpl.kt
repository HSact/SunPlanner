package com.hsact.sunplanner.data.repository

import com.hsact.sunplanner.data.network.OpenMeteoGeo
import com.hsact.sunplanner.data.network.OpenMeteoService
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.repository.WeatherRepository
import javax.inject.Inject

/**
 * Implementation of [WeatherRepository] using OpenMeteo API services.
 *
 * Provides methods to fetch historical weather data and city coordinates.
 *
 * @property service Service for weather data requests.
 * @property geolocationService Service for geolocation and city coordinate lookups.
 */
class WeatherRepositoryImpl @Inject constructor(
    private val service: OpenMeteoService,
    private val geolocationService: OpenMeteoGeo
) : WeatherRepository {
    /**
     * Fetches historical weather data for the specified parameters.
     *
     * @param params Weather request parameters including coordinates, date range and units.
     * @return Weather data response from the API.
     * @throws Exception if the network request fails.
     */
    override suspend fun getWeather(params: WeatherRequestParams) =
        try {
            service.getHistoricalWeather(
                latitude = params.latitude,
                longitude = params.longitude,
                startDate = params.startDate,
                endDate = params.endDate,
                temperatureUnit = params.temperatureUnit,
                windSpeedUnit = params.windSpeedUnit,
                precipitationUnit = params.precipitationUnit
            )
        } catch (e: Exception) {
            throw e
        }

    /**
     * Retrieves geographic coordinates for a given city name.
     *
     * @param cityName The name of the city to search for.
     * @return The first matching [Location], or null if none found.
     * @throws Exception if the request fails.
     */
    override suspend fun getCoordinatesByCity(cityName: String): Location? {
        return try {
            val response = geolocationService.getCityCoordinates(cityName)
            response.results?.firstOrNull()
        } catch (e: Exception) {
            throw Exception("Error fetching coordinates: ${e.message}")
        }
    }

    /**
     * Retrieves a list of cities matching the given name and language.
     *
     * @param cityName The city name to search for.
     * @param language Language code to localize the search results.
     * @return A list of matching [Location] objects, or null if none found.
     * @throws Exception if the request fails.
     */
    override suspend fun getCitiesList(cityName: String, language: String): List<Location>? {
        return try {
            val response = geolocationService.getCityCoordinates(cityName, language = language)
            response.results
        } catch (e: Exception) {
            throw Exception("Error fetching cities: ${e.message}")
        }
    }
}