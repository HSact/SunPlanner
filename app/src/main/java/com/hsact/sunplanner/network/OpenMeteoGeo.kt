package com.hsact.sunplanner.network

import com.hsact.sunplanner.data.responses.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoGeo {
    @GET("v1/search")
    suspend fun getCityCoordinates(
        @Query("name") cityName: String,
        @Query("count") count: Int = 10
        //@Query("language") language: String = "en",
        //@Query("format") format: String = "json"
    ): LocationResponse
}