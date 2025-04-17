package com.hsact.sunplanner.domain.model

data class DailyAggregatedData(
    val date: String, // "MM-dd"
    val avgMaxTemp: Double,
    val avgMinTemp: Double,
    val avgSunshineSeconds: Double,
    val avgPrecipitation: Double,
    val avgWindSpeed: Double
)