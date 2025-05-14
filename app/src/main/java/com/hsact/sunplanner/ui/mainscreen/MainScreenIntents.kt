package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location

sealed class MainScreenIntents {
    data class FetchCityList(val query: String) : MainScreenIntents()
    data class UpdateLocation(val city: Location) : MainScreenIntents()
    data class UpdateStartYear(val year: Int) : MainScreenIntents()
    data class UpdateStartMonth(val month: Int) : MainScreenIntents()
    data class UpdateStartDay(val day: Int) : MainScreenIntents()
    data class UpdateEndYear(val year: Int) : MainScreenIntents()
    data class UpdateEndMonth(val month: Int) : MainScreenIntents()
    data class UpdateEndDay(val day: Int) : MainScreenIntents()
    object CleanError : MainScreenIntents()
    object WeatherSearchClick : MainScreenIntents()
}