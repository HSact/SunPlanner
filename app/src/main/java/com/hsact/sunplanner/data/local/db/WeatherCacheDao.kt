package com.hsact.sunplanner.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherCacheDao {

    @Query("SELECT * FROM weather_cache WHERE id = :id")
    suspend fun getCachedWeather(id: String): CachedWeather?

    @Query("""
        SELECT * FROM weather_cache 
        WHERE ABS(latitude - :latitude) < 0.001 
          AND ABS(longitude - :longitude) < 0.001 
          AND tempUnit = :tempUnit 
          AND windUnit = :windUnit 
          AND precipUnit = :precipUnit 
        ORDER BY timestamp DESC
    """)
    suspend fun getCachedWeatherForLocation(
        latitude: Double,
        longitude: Double,
        tempUnit: String,
        windUnit: String,
        precipUnit: String
    ): List<CachedWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(cachedWeather: CachedWeather)

    @Query("DELETE FROM weather_cache WHERE id NOT IN (SELECT id FROM weather_cache ORDER BY timestamp DESC LIMIT 50)")
    suspend fun clearOldCache()

    @Query("DELETE FROM weather_cache")
    suspend fun clearAll()
}
