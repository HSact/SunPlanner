package com.hsact.sunplanner.domain.model

/**
 * Represents aggregated weather data for a specific day across multiple years.
 */
data class DailyAggregatedData(
    val date: String, // "MM-dd"
    val avgMaxTemp: Double,
    val avgAvgTemp: Double,
    val avgMinTemp: Double,
    val avgSunshineSeconds: Double,
    val avgDaylightSeconds: Double,
    val avgPrecipitation: Double,
    val avgWindSpeed: Double,
    val avgWindGustSpeed: Double,
    val avgAqi: Double,
    val commonWeatherCode: Int
)
