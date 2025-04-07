package com.hsact.sunplanner.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse

data class MainUIState (
    val cityName: String = "",
    var cities: List<Location> = emptyList(),
    var dates: DateAdapter = DateAdapter(),
    val location: Location? = null,
    var startYear: Int = 0,
    var endYear: Int = 0,
    val years: Int = endYear - startYear,
    val startDate: String = "",
    val endDate: String = "",
    val weatherData: WeatherResponse? = null
)