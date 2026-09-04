package com.hsact.sunplanner.di

import android.content.Context
import androidx.room.Room
import com.hsact.sunplanner.data.local.db.AppDatabase
import com.hsact.sunplanner.data.local.db.WeatherCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sunplanner_db"
        ).build()
    }

    @Provides
    fun provideWeatherCacheDao(database: AppDatabase): WeatherCacheDao {
        return database.weatherCacheDao()
    }
}
