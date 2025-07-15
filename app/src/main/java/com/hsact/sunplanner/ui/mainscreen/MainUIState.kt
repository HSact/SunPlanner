package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.model.DatesBundle
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.WeatherMetrics
import java.time.LocalDate

data class MainUIState(
    val settingsBundle: SettingsBundle = SettingsBundle(),
    val maxYearRange: Int = 30,
    val validationError: String? = null,
    val networkError: ApiError? = null,
    val networkErrorId: String? = null,
    val isLoading: Boolean = false,
    val isOneDay: Boolean = true,
    val isOneYear: Boolean = false,
    val cityName: String = "",
    val cities: List<Location> = emptyList(),
    val tempDates: DatesBundle = DatesBundle(LocalDate.now().minusYears(10), LocalDate.now().minusYears(1)),
    val confirmedDates: DatesBundle = tempDates,
    val weatherData: WeatherResponse? = null,
    val weatherMetrics: WeatherMetrics = WeatherMetrics(),
) {
    fun isLocationNotNull(): Boolean {
        return settingsBundle.location != null
    }
}