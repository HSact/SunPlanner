package com.hsact.sunplanner

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse (
    val latitude: Double,
    val longitude: Double,
)

@JsonClass(generateAdapter = true)
data class Hourly(
    val time: List<String>,
    @Json(name = "temperature_2m") val temperature2m: List<Double>
)