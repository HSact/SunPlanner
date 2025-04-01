package com.hsact.sunplanner.network

import com.hsact.sunplanner.OpenMeteoService
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private val moshi = Moshi.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://archive-api.open-meteo.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: OpenMeteoService by lazy {
        retrofit.create(OpenMeteoService::class.java)
    }
}
