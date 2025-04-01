package com.hsact.sunplanner.data

import com.squareup.moshi.Json

data class LocationResponse(
    @Json(name = "results") val results: List<Location>?
)

data class Location(
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "country") val country: String?
)
