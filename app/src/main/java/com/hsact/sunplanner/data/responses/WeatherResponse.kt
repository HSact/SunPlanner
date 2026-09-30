package com.hsact.sunplanner.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a weather response from the Open-Meteo API containing metadata and daily forecast data.
 */
@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val daily: DailyWeather
)

/**
 * Represents a collection of daily weather parameters from the Open-Meteo API.
 */
@Serializable
data class DailyWeather(
    val time: List<String>,
    @SerialName("weather_code") val code: List<Double>,
    @SerialName("temperature_2m_max") val maxTemperature: List<Double>,
    @SerialName("temperature_2m_min") val minTemperature: List<Double>,
    @SerialName("apparent_temperature_max") val apparentMaxTemperature: List<Double>,
    @SerialName("apparent_temperature_min") val apparentMinTemperature: List<Double>,
    @SerialName("precipitation_sum") val precipitationSum: List<Double>,
    @SerialName("precipitation_hours") val precipitationHours: List<Double>,
    @SerialName("sunshine_duration") val sunshineDuration: List<Double>,
    @SerialName("daylight_duration") val daylightDuration: List<Double>,
    @SerialName("wind_speed_10m_max") val windSpeedMax: List<Double>,
    @SerialName("wind_gusts_10m_max") val windGustsMax: List<Double>,
    @SerialName("european_aqi") val european_aqi: List<Double?> = emptyList()
)
