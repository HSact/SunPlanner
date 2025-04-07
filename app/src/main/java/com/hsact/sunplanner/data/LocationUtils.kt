package com.hsact.sunplanner.data

import com.hsact.sunplanner.data.responses.Location
import java.time.LocalDate

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
    fun prepareDate(year: Int, month: String, day: Int): String {
        return "$year-$month-$day"
    }
}