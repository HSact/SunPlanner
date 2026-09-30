package com.hsact.sunplanner.domain.model

import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Line

/**
 * Container for all weather-related graph data used in the UI layer.
 */
data class WeatherGraphData(
    var maxTemperature: Line? = null,
    var avgTemperature: Line? = null,
    var minTemperature: Line? = null,
    var dayLightDuration: Line? = null,
    var sunShineDuration: Line? = null,
    var precipitation: List<Bars> = emptyList(),
    var windSpeed: Line? = null,
    var windGustsSpeed: Line? = null,
    var airQuality: Line? = null,
    var airQualityComp: Line? = null
)
