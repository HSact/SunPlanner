package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Line
import java.time.LocalDate

data class MainUIState (
    var error : String = "",
    var isOneDay: Boolean = true,
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
    var confirmedStartLD: LocalDate = LocalDate.now().minusYears(10),
    var confirmedEndLD: LocalDate = LocalDate.now().minusYears(1),
    var weatherData: WeatherResponse? = null,
    var maxTemperature: Line? = null,
    var minTemperature: Line? = null,
    var sunDuration: Line? = null,
    var precipitation: Bars? = null
)