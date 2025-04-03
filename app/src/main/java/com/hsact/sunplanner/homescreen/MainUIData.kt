package com.hsact.sunplanner.homescreen

import com.hsact.sunplanner.data.Location

data class MainUIData (
    val cityName: String = "",
    var cities: List<Location> = emptyList(),
    val years: Int = 0,
    val startDate: String = "",
    val endDate: String = ""
)