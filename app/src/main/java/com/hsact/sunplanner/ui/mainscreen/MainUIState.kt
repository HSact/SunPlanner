package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import java.time.LocalDate

data class MainUIState (
    val cityName: String = "",
    var cities: List<Location> = emptyList(),
    var dates: DateAdapter = DateAdapter(),
    val location: Location? = null,
    var startYear: Int = 0,
    var endYear: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    var startLD: LocalDate = LocalDate.now().minusYears(10),
    var endLD: LocalDate = LocalDate.now().minusYears(1),
    var weatherData: WeatherResponse? = null
)