package com.hsact.sunplanner.data.utils

import com.hsact.sunplanner.data.responses.Location

/**
 * Utility object for working with location-related data.
 */
object LocationUtils {
    /**
     * Builds a full, human-readable city name from the location object by combining all available
     * administrative levels and country name into a comma-separated string.
     *
     * The order is:
     * name, admin2, admin3, admin4, admin1, country — where each component may be null.
     *
     * @param city A [Location] object containing city and administrative data.
     * @return A string representing the full name of the location.
     */
    fun buildCityFullName(city: Location): String {
        return listOfNotNull(
            city.name,
            city.admin2,
            city.admin3,
            city.admin4,
            city.admin1,
            city.country
        ).joinToString(", ")
    }
}