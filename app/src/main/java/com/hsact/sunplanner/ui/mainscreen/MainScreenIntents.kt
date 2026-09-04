package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.model.Bookmark

sealed class MainScreenIntents {
    data class FetchCityList(val query: String) : MainScreenIntents()
    data class UpdateCityName(val name: String) : MainScreenIntents()
    data class UpdateLocation(val city: Location) : MainScreenIntents()
    data class UpdateStartYear(val year: Int) : MainScreenIntents()
    data class UpdateStartMonth(val month: Int) : MainScreenIntents()
    data class UpdateStartDay(val day: Int) : MainScreenIntents()
    data class UpdateEndYear(val year: Int) : MainScreenIntents()
    data class UpdateEndMonth(val month: Int) : MainScreenIntents()
    data class UpdateEndDay(val day: Int) : MainScreenIntents()
    data object CleanValidationError : MainScreenIntents()
    data object CleanNetworkError : MainScreenIntents()
    data object WeatherSearchClick : MainScreenIntents()

    data object ToggleBookmark : MainScreenIntents()
    data class SelectBookmark(val bookmark: Bookmark) : MainScreenIntents()
    data class DeleteBookmark(val id: String) : MainScreenIntents()

    data object UseCurrentLocation : MainScreenIntents()
    data object ClearAppCache : MainScreenIntents()

    data object ToggleComparisonMode : MainScreenIntents()
    data class UpdateComparisonLocation(val city: Location) : MainScreenIntents()
    data object RemoveComparison : MainScreenIntents()
}
