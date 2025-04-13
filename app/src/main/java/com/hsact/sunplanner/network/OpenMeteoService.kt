package com.hsact.sunplanner.network

import com.hsact.sunplanner.data.responses.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoService {
    @GET("v1/archive")
    suspend fun getHistoricalWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,precipitation_sum,precipitation_hours,sunshine_duration,wind_speed_10m_max,wind_gusts_10m_max",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}