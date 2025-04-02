package com.hsact.sunplanner

import com.hsact.sunplanner.data.Location

data class MainUIData (
    val cityName: String,
    var cities: List<Location>, //= emptyList()
    val years: Int,
    val startDate: String,
    val endDate: String
)