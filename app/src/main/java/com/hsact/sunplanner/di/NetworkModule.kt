package com.hsact.sunplanner.di

import com.hsact.sunplanner.data.network.OpenMeteoGeo
import com.hsact.sunplanner.data.network.OpenMeteoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @Named("WeatherRetrofit")
    fun provideWeatherRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://archive-api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("GeoRetrofit")
    fun provideGeoRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenMeteoService(
        @Named("WeatherRetrofit") retrofit: Retrofit
    ): OpenMeteoService {
        return retrofit.create(OpenMeteoService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenMeteoGeo(
        @Named("GeoRetrofit") geoRetrofit: Retrofit
    ): OpenMeteoGeo {
        return geoRetrofit.create(OpenMeteoGeo::class.java)
    }
}