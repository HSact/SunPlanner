package com.hsact.sunplanner.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedWeather::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherCacheDao(): WeatherCacheDao
}
