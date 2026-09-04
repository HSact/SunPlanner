package com.hsact.sunplanner.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing cached weather data from the API.
 * The [id] is a composite key: "lat_lon_startDate_endDate".
 */
@Entity(tableName = "weather_cache")
data class CachedWeather(
    @PrimaryKey val id: String,
    val jsonResponse: String,
    val timestamp: Long = System.currentTimeMillis()
)
