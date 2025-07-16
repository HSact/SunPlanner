package com.hsact.sunplanner.di

import com.hsact.sunplanner.data.network.OpenMeteoGeo
import com.hsact.sunplanner.data.network.OpenMeteoService
import com.hsact.sunplanner.data.repository.WeatherRepositoryImpl
import com.hsact.sunplanner.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides network-related dependencies such as Retrofit instances,
 * API services, and repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a singleton [Retrofit] instance configured for accessing weather data.
     *
     * @return A [Retrofit] instance with the base URL for historical weather data.
     */
    @Provides
    @Singleton
    @Named("WeatherRetrofit")
    fun provideWeatherRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://archive-api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    /**
     * Provides a singleton [Retrofit] instance configured for accessing geolocation data.
     *
     * @return A [Retrofit] instance with the base URL for geolocation services.
     */
    @Provides
    @Singleton
    @Named("GeoRetrofit")
    fun provideGeoRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    /**
     * Provides the [OpenMeteoService] API interface for weather data requests.
     *
     * @param retrofit A [Retrofit] instance for weather API.
     * @return An implementation of [OpenMeteoService].
     */
    @Provides
    @Singleton
    fun provideOpenMeteoService(
        @Named("WeatherRetrofit") retrofit: Retrofit
    ): OpenMeteoService {
        return retrofit.create(OpenMeteoService::class.java)
    }

    /**
     * Provides the [OpenMeteoGeo] API interface for geolocation requests.
     *
     * @param geoRetrofit A [Retrofit] instance for geolocation API.
     * @return An implementation of [OpenMeteoGeo].
     */
    @Provides
    @Singleton
    fun provideOpenMeteoGeo(
        @Named("GeoRetrofit") geoRetrofit: Retrofit
    ): OpenMeteoGeo {
        return geoRetrofit.create(OpenMeteoGeo::class.java)
    }

    /**
     * Provides an implementation of the [WeatherRepository] interface.
     *
     * @param service The weather API service.
     * @param geoService The geolocation API service.
     * @return A [WeatherRepositoryImpl] instance.
     */
    @Provides
    @Singleton
    fun provideWeatherRepository(
        service: OpenMeteoService,
        geoService: OpenMeteoGeo
    ): WeatherRepository {
        return WeatherRepositoryImpl(service, geoService)
    }
}