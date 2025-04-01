package com.hsact.sunplanner

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse (
    val latitude: Double,
    val longitude: Double,
    val daily: DailyWeather
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    val time: List<String>,
    @Json(name = "temperature_2m_max") val maxTemperature: List<Double>,
    @Json(name = "temperature_2m_min") val minTemperature: List<Double>
)