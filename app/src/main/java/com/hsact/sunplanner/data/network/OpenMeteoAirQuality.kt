package com.hsact.sunplanner.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for Air Quality data.
 */
interface OpenMeteoAirQuality {
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("hourly") hourly: String = "european_aqi",
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponse
}

@Serializable
data class AirQualityResponse(
    val hourly: AirQualityHourly
)

@Serializable
data class AirQualityHourly(
    val time: List<String>,
    val european_aqi: List<Double?>
)
