package com.hsact.sunplanner.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a weather response from the Open-Meteo API containing metadata and daily forecast data.
 *
 * @property latitude Latitude of the requested location.
 * @property longitude Longitude of the requested location.
 * @property daily Daily weather data.
 */
@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val daily: DailyWeather
)

/**
 * Represents a collection of daily weather parameters from the Open-Meteo API.
 *
 * Each list corresponds to daily values in ISO 8601 date order.
 *
 * @property time List of ISO 8601 dates ("yyyy-MM-dd").
 * @property code Weather condition codes (as strings).
 * @property maxTemperature Daily maximum air temperature at 2 meters (°C or °F depending on unit).
 * @property minTemperature Daily minimum air temperature at 2 meters (°C or °F depending on unit).
 * @property apparentMaxTemperature Daily maximum *feels like* temperature (°C or °F).
 * @property apparentMinTemperature Daily minimum *feels like* temperature (°C or °F).
 * @property precipitationSum Total daily precipitation (mm or inches).
 * @property precipitationHours Number of hours with precipitation per day.
 * @property sunshineDuration Duration of sunshine per day in seconds.
 * @property daylightDuration Total daylight duration per day in seconds.
 * @property windSpeedMax Maximum average wind speed per day at 10m height (km/h, m/s, mph, or kn).
 * @property windGustsMax Maximum wind gusts per day at 10m height (km/h, m/s, mph, or kn).
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
)