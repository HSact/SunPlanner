package com.hsact.sunplanner.data.network

import com.hsact.sunplanner.data.responses.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for accessing geolocation data
 * from the Open-Meteo geocoding API.
 */
interface OpenMeteoGeo {

    /**
     * Searches for geographical coordinates of a city by its name.
     *
     * @param cityName The name of the city to search for.
     * @param count The maximum number of location results to return. Default is 10.
     * @param language Language code for the location names (e.g., "en", "ru").
     *
     * @return A [LocationResponse] containing a list of possible matches.
     */
    @GET("v1/search")
    suspend fun getCityCoordinates(
        @Query("name") cityName: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en"
    ): LocationResponse
}