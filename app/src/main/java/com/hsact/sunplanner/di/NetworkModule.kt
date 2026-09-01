package com.hsact.sunplanner.di

import com.hsact.sunplanner.data.network.OpenMeteoGeo
import com.hsact.sunplanner.data.network.OpenMeteoService
import com.hsact.sunplanner.data.repository.WeatherRepositoryImpl
import com.hsact.sunplanner.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    companion object {
        private const val WEATHER_BASE_URL = "https://archive-api.open-meteo.com/"
        private const val GEO_BASE_URL = "https://geocoding-api.open-meteo.com/"

        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            ignoreUnknownKeys = true
        }

        @Provides
        @Singleton
        fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        @Provides
        @Singleton
        @Named("WeatherRetrofit")
        fun provideWeatherRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
            return Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        }

        @Provides
        @Singleton
        @Named("GeoRetrofit")
        fun provideGeoRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
            return Retrofit.Builder()
                .baseUrl(GEO_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        }

        @Provides
        @Singleton
        fun provideOpenMeteoService(
            @Named("WeatherRetrofit") retrofit: Retrofit
        ): OpenMeteoService = retrofit.create(OpenMeteoService::class.java)

        @Provides
        @Singleton
        fun provideOpenMeteoGeo(
            @Named("GeoRetrofit") retrofit: Retrofit
        ): OpenMeteoGeo = retrofit.create(OpenMeteoGeo::class.java)
    }
}
