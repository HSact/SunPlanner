package com.hsact.sunplanner.domain.model

/**
 * Represents aggregated weather data for a specific day across multiple years.
 * Used for calculating and visualizing long-term averages.
 *
 * @property date The date in "MM-dd" format representing the day of the year.
 * @property avgMaxTemp The average of the maximum daily temperatures.
 * @property avgAvgTemp The average of the mean daily temperatures.
 * @property avgMinTemp The average of the minimum daily temperatures.
 * @property avgSunshineSeconds The average sunshine duration per day in seconds.
 * @property avgDaylightSeconds The average daylight duration per day in seconds.
 * @property avgPrecipitation The average daily precipitation in selected units.
 * @property avgWindSpeed The average daily wind speed in selected units.
 * @property avgWindGustSpeed The average daily wind gust speed in selected units.
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
    val avgWindGustSpeed: Double
)