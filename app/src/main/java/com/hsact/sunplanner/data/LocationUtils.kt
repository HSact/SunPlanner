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
    fun prepareDate(year: Int, date: LocalDate): String {
        val month = date.monthValue.toString().padStart(2, '0')
        val day = date.dayOfMonth.toString().padStart(2, '0')
        return "$year-$month-$day"
    }
}