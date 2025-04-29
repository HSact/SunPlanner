package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.ThemeMode
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Line
import java.time.LocalDate

data class MainUIState (
    var languageMode: LanguageMode = LanguageMode.ENGLISH,
    var themeMode: ThemeMode = ThemeMode.SYSTEM,
    var error : String = "",
    var isLoading: Boolean = false,
    var isOneDay: Boolean = true,
    var isOneYear: Boolean = false,
    val cityName: String = "",
    var cities: List<Location> = emptyList(),
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
    var precipitation: Bars? = null,
    var windSpeed: Line? = null,
    var windGustsSpeed: Line? = null
)