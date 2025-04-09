package com.hsact.sunplanner.ui.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.network.RetrofitInstance
import com.hsact.sunplanner.network.WeatherRequestParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel : ViewModel() {
    private val repository =
        WeatherRepository(RetrofitInstance.WeatherApi, RetrofitInstance.GeolocationApi)

    private val _searchDataUI = MutableStateFlow(MainUIState())
    val searchDataUI: StateFlow<MainUIState> get() = _searchDataUI

    fun saveLocationToVM(city: Location) {
        _searchDataUI.value = _searchDataUI.value.copy(location = city)
    }

    fun updateStartYear(year: Int) {
        val old = _searchDataUI.value.startLD
        val newDate = old.withYear(year).coerceDay()
        _searchDataUI.value = _searchDataUI.value.copy(startLD = newDate)
    }

    fun updateStartMonth(month: Int) {
        val old = _searchDataUI.value.startLD
        val newDate = old.withMonth(month).coerceDay()
        _searchDataUI.value = _searchDataUI.value.copy(startLD = newDate)
    }

    fun updateStartDay(day: Int) {
        val old = _searchDataUI.value.startLD
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _searchDataUI.value = _searchDataUI.value.copy(startLD = newDate)
    }
    fun updateEndYear(year: Int) {
        val old = _searchDataUI.value.endLD
        val newDate = old.withYear(year).coerceDay()
        _searchDataUI.value = _searchDataUI.value.copy(endLD = newDate)
    }

    fun updateEndMonth(month: Int) {
        val old = _searchDataUI.value.endLD
        val newDate = old.withMonth(month).coerceDay()
        _searchDataUI.value = _searchDataUI.value.copy(endLD = newDate)
    }

    fun updateEndDay(day: Int) {
        val old = _searchDataUI.value.endLD
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _searchDataUI.value = _searchDataUI.value.copy(endLD = newDate)
    }

    fun LocalDate.coerceDay(): LocalDate {
        val maxDay = this.lengthOfMonth()
        return if (this.dayOfMonth > maxDay) this.withDayOfMonth(maxDay) else this
    }

    private fun prepareDate(month: String, day: Int): String {
        val monthNumber = when (month) {
            "January" -> "01"
            "February" -> "02"
            "March" -> "03"
            "April" -> "04"
            "May" -> "05"
            "June" -> "06"
            "July" -> "07"
            "August" -> "08"
            "September" -> "09"
            "October" -> "10"
            "November" -> "11"
            "December" -> "12"
            else -> "00"
        }
        val dayFormatted = day.toString().padStart(2, '0')
        return "$monthNumber-$dayFormatted"
    }

    fun prepareParamsForRequest() { //TODO: rewrite
        val location = _searchDataUI.value.location
        val startDate = _searchDataUI.value.startLD
        val endDate = _searchDataUI.value.endLD

        if (location == null || startDate > endDate) {
            // TODO: Show error message
            return
        }
        val params = WeatherRequestParams().apply {
            latitude = location.latitude
            longitude = location.longitude
            this.startDate = startDate.toString() // YYYY-MM-DD
            this.endDate = endDate.toString()
        }
        fetchWeather(params)
    }

    fun fetchCityList(cityName: String) {
        var cities: List<Location>? = null
        viewModelScope.launch {
            cities = repository.getCitiesList(
                cityName = cityName
            )
            if (cities != null) {
                _searchDataUI.value = _searchDataUI.value.copy(cities = cities!!)
                println(cities)
            }
        }
    }

    private fun fetchWeather(params: WeatherRequestParams) {
        viewModelScope.launch {
            try {
                val response = repository.getWeather(
                    latitude = params.latitude,
                    longitude = params.longitude,
                    startDate = params.startDate,
                    endDate = params.endDate
                )
                println(response)
                _searchDataUI.value = _searchDataUI.value.copy(
                    weatherData = response
                )
            } catch (e: Exception) {
                println("Error fetching weather: ${e.message}")
            }
        }
    }

    /*fun fetchWeatherByCity(cityName: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val location = repository.getCoordinatesByCity(
                cityName = cityName
            )
            val params = WeatherRequestParams()
            params.latitude = location?.latitude ?: 0.0
            params.longitude = location?.longitude ?: 0.0
            params.startDate = startDate
            params.endDate = endDate
            if (location != null) {
                fetchWeather(params)
            } else {
                println("Error fetching coordinates")
            }
        }
    }*/
}