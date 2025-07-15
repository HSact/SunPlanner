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
    @param:Json(name = "weather_code") val code: List<String>,
    @param:Json(name = "temperature_2m_max") val maxTemperature: List<Double>,
    @param:Json(name = "temperature_2m_min") val minTemperature: List<Double>,
    @param:Json(name = "apparent_temperature_max") val apparentMaxTemperature: List<Double>,
    @param:Json(name = "apparent_temperature_min") val apparentMinTemperature: List<Double>,
    @param:Json(name = "precipitation_sum") val precipitationSum: List<Double>,
    @param:Json(name = "precipitation_hours") val precipitationHours: List<Double>,
    @param:Json(name = "sunshine_duration") val sunshineDuration: List<Double>,
    @param:Json(name = "daylight_duration") val daylightDuration: List<Double>,
    @param:Json(name = "wind_speed_10m_max") val windSpeedMax: List<Double>,
    @param:Json(name = "wind_gusts_10m_max") val windGustsMax: List<Double>,
)