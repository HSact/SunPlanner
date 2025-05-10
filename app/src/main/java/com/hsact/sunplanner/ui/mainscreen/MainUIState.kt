package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.WeatherGraphData
import java.time.LocalDate

data class MainUIState (
    val settingsBundle: SettingsBundle = SettingsBundle(),
    val error : String = "",
    val isLoading: Boolean = false,
    val isOneDay: Boolean = true,
    val isOneYear: Boolean = false,
    val cityName: String = "",
    val cities: List<Location> = emptyList(),
    val startLD: LocalDate = LocalDate.now().minusYears(10),
    val endLD: LocalDate = LocalDate.now().minusYears(1),
    val confirmedStartLD: LocalDate = startLD,
    val confirmedEndLD: LocalDate = endLD,
    val weatherData: WeatherResponse? = null,
    val weatherGraphData: WeatherGraphData = WeatherGraphData()
)