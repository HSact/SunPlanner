package com.hsact.sunplanner.data.responses

import com.squareup.moshi.JsonClass

/**
 * Represents a response from the geolocation API containing a list of possible location matches.
 *
 * @property results A list of [Location] entries returned by the API. Can be null if no matches found.
 */
@JsonClass(generateAdapter = true)
data class LocationResponse(
    val results: List<Location>?
)

/**
 * Represents a single location returned by the geolocation API.
 *
 * @property id Unique identifier of the location.
 * @property name Name of the location, usually a city or populated place.
 * @property latitude Latitude of the location.
 * @property longitude Longitude of the location.
 * @property country Optional country name.
 * @property admin1 Optional administrative level 1 (e.g., region, state).
 * @property admin2 Optional administrative level 2 (e.g., county).
 * @property admin3 Optional administrative level 3 (e.g., district).
 * @property admin4 Optional administrative level 4 (e.g., municipality).
 */
@JsonClass(generateAdapter = true)
data class Location(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?,
    val admin2: String?,
    val admin3: String?,
    val admin4: String?,
)