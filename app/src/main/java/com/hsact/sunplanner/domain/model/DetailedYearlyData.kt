package com.hsact.sunplanner.domain.model

/**
 * Data class representing weather data for a specific year in the detailed view.
 */
data class DetailedYearlyData(
    val year: Int,
    val metrics: WeatherMetrics,
    val dateLabels: List<String>
)
