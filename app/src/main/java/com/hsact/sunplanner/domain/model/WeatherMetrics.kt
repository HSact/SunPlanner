package com.hsact.sunplanner.domain.model

data class WeatherMetrics(
    var maxTemps: List <Double> = emptyList(),
    var averageTemps: List <Double> = emptyList(),
    var minTemps: List <Double> = emptyList(),
    var sunshine: List <Double> = emptyList(),
    var dayLight: List <Double> = emptyList(),
    var precipitation: List <Double> = emptyList(),
    var windSpeed: List <Double> = emptyList(),
    var gustSpeed: List <Double> = emptyList()
    )