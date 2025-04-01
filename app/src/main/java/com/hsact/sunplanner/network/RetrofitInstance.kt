package com.hsact.sunplanner.network

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private val moshi = Moshi.Builder().build()

    private val retrofitWeather = Retrofit.Builder()
        .baseUrl("https://archive-api.open-meteo.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val retrofitGeolocation = Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val WeatherApi: OpenMeteoService by lazy {
        retrofitWeather.create(OpenMeteoService::class.java)
    }

    val GeolocationApi: OpenMeteoGeo by lazy {
        retrofitGeolocation.create(OpenMeteoGeo::class.java)
    }
}