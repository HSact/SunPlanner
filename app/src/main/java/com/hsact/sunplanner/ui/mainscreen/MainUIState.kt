package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.model.DatesBundle
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.WeatherGraphData
import java.time.LocalDate

data class MainUIState(
    val settingsBundle: SettingsBundle = SettingsBundle(),
    val maxYearRange: Int = 30,
    val error: String = "",
    val isLoading: Boolean = false,
    val isOneDay: Boolean = true,
    val isOneYear: Boolean = false,
    val cityName: String = "",
    val cities: List<Location> = emptyList(),
    val tempDates: DatesBundle = DatesBundle(LocalDate.now().minusYears(10), LocalDate.now().minusYears(1)),
    val confirmedDates: DatesBundle = tempDates,
    val weatherData: WeatherResponse? = null,
    val weatherGraphData: WeatherGraphData = WeatherGraphData()
) {
    fun isLocationNotNull(): Boolean {
        return settingsBundle.location != null
    }
}