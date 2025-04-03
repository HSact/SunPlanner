package com.hsact.sunplanner.mainscreen

import com.hsact.sunplanner.data.responses.Location

data class MainUIData (
    val cityName: String = "",
    var cities: List<Location> = emptyList(),
    val location: Location? = null,
    val years: Int = 0,
    val startDate: String = "",
    val endDate: String = ""
)