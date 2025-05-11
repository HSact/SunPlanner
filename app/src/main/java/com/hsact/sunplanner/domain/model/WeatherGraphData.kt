package com.hsact.sunplanner.domain.model

import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Line

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