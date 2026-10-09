package com.hsact.sunplanner.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing cached weather data from the API.
 * The [id] is a composite key: "lat_lon_startDate_endDate_tempUnit_windUnit_precipUnit".
 */
@Entity(tableName = "weather_cache")
data class CachedWeather(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val startDate: String,
    val endDate: String,
    val tempUnit: String,
    val windUnit: String,
    val precipUnit: String,
    val jsonResponse: String,
    val timestamp: Long = System.currentTimeMillis()
)
