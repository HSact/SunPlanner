package com.hsact.sunplanner.data.responses

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
    @Json(name = "weather_code") val code: List<String>,
    @Json(name = "temperature_2m_max") val maxTemperature: List<Double>,
    @Json(name = "temperature_2m_min") val minTemperature: List<Double>,
    @Json(name = "apparent_temperature_max") val apparentMaxTemperature: List<Double>,
    @Json(name = "apparent_temperature_min") val apparentMinTemperature: List<Double>,
    @Json(name = "precipitation_sum") val precipitationSum: List<Double>,
    @Json(name = "precipitation_hours") val precipitationHours: List<Double>,
    @Json(name = "sunshine_duration") val sunshineDuration: List<Double>,
    @Json(name = "wind_speed_10m_max") val windSpeedMax: List<Double>,
    @Json(name = "wind_gusts_10m_max") val windGustsMax: List<Double>,
)