package com.hsact.sunplanner.domain.model

import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Line

/**
 * Container for all weather-related graph data used in the UI layer.
 * Includes various types of metrics such as temperature, precipitation, sunlight, and wind.
 *
 * @property maxTemperature Graph line representing the maximum daily temperature.
 * @property avgTemperature Graph line representing the average daily temperature.
 * @property minTemperature Graph line representing the minimum daily temperature.
 * @property dayLightDuration Graph line representing the total daylight duration per day.
 * @property sunShineDuration Graph line representing the sunshine duration per day.
 * @property precipitation Bar graph representing the total precipitation per day.
 * @property windSpeed Graph line representing the average wind speed per day.
 * @property windGustsSpeed Graph line representing the maximum wind gusts per day.
 */
data class WeatherGraphData(
    var maxTemperature: Line? = null,
    var avgTemperature: Line? = null,
    var minTemperature: Line? = null,
    var dayLightDuration: Line? = null,
    var sunShineDuration: Line? = null,
    var precipitation: Bars? = null,
    var windSpeed: Line? = null,
    var windGustsSpeed: Line? = null
)