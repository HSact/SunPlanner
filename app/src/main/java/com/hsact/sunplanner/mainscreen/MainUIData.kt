package com.hsact.sunplanner.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse

data class MainUIData (
    val cityName: String = "",
    var cities: List<Location> = emptyList(),
    val location: Location? = null,
    val startYear: Int = 0,
    val endYear: Int = 0,
    val years: Int = endYear - startYear,
    val startDate: String = "",
    val endDate: String = "",
    val weatherData: WeatherResponse? = null
)