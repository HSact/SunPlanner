package com.hsact.sunplanner.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationResponse(
    val results: List<Location>?
)

@JsonClass(generateAdapter = true)
data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?
)