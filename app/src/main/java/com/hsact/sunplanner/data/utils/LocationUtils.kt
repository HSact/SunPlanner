package com.hsact.sunplanner.data.utils

import com.hsact.sunplanner.data.responses.Location

object LocationUtils {
    fun buildCityFullName(city: Location): String {
        return listOfNotNull(
            city.name,
            city.admin1,
            city.admin2,
            city.admin3,
            city.admin4,
            city.country
        ).joinToString(", ")
    }
}