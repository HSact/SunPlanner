package com.hsact.sunplanner.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.network.RetrofitInstance
import com.hsact.sunplanner.network.WeatherRequestParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class DateField {
    START_YEAR, END_YEAR, START_MONTH, END_MONTH, START_DAY, END_DAY
}
class MainViewModel : ViewModel() {
    private val repository =
        WeatherRepository(RetrofitInstance.WeatherApi, RetrofitInstance.GeolocationApi)

    private val _searchDataUI = MutableStateFlow(MainUIState())
    val searchDataUI: StateFlow<MainUIState> get() = _searchDataUI

    fun saveLocationToVM(city: Location) {
        _searchDataUI.value = _searchDataUI.value.copy(location = city)
    }
    /*fun saveStartYearToVM(year: Int) {
        _searchDataUI.value.dates = _searchDataUI.value.dates.copy(startYear = year)
    }
    fun saveEndYearToVM(year: Int) {
        _searchDataUI.value.dates = _searchDataUI.value.dates.copy(endYear = year)
    }*/
    fun saveDateFieldToVM(field: DateField, value: Any) {
        _searchDataUI.value.dates = when (field) {
            DateField.START_YEAR -> {
                val year = value as? Int ?: value.toString().toIntOrNull()
                if (year != null) _searchDataUI.value.dates.copy(startYear = year) else _searchDataUI.value.dates
            }
            DateField.END_YEAR -> {
                val year = value as? Int ?: value.toString().toIntOrNull()
                if (year != null) _searchDataUI.value.dates.copy(endYear = year) else _searchDataUI.value.dates
            }
            DateField.START_MONTH -> {
                val month = value as? String ?: value.toString()
                _searchDataUI.value.dates.copy(startMonth = month)
            }
            DateField.END_MONTH -> {
                val month = value as? String ?: value.toString()
                _searchDataUI.value.dates.copy(endMonth = month)
            }
            DateField.START_DAY -> {
                val day = value as? Int ?: value.toString().toIntOrNull()
                if (day != null) _searchDataUI.value.dates.copy(startDay = day) else _searchDataUI.value.dates
            }
            DateField.END_DAY -> {
                val day = value as? Int ?: value.toString().toIntOrNull()
                if (day != null) _searchDataUI.value.dates.copy(endDay = day) else _searchDataUI.value.dates
            }
        }
        _searchDataUI.value = _searchDataUI.value.copy(startDate =  //TODO: rewrite
            prepareDate(_searchDataUI.value.dates.startYear, _searchDataUI.value.dates.startMonth, _searchDataUI.value.dates.startDay))
        _searchDataUI.value = _searchDataUI.value.copy(endDate =
            prepareDate(_searchDataUI.value.dates.endYear, _searchDataUI.value.dates.endMonth, _searchDataUI.value.dates.endDay))
    }
    private fun prepareDate(year: Int, month: String, day: Int): String {
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
            else -> "00" //throw IllegalArgumentException("Invalid month name")
        }
        val dayFormatted = day.toString().padStart(2, '0')
        return "$year-$monthNumber-$dayFormatted"
    }

    fun prepareParamsForRequest() { //TODO: rewrite
        val params = WeatherRequestParams()
        params.latitude = _searchDataUI.value.location?.latitude ?: 0.0
        params.longitude = _searchDataUI.value.location?.longitude ?: 0.0
        params.startDate = _searchDataUI.value.startDate
        params.endDate = _searchDataUI.value.endDate
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
                _searchDataUI.value = _searchDataUI.value.copy(weatherData = response)
            } catch (e: Exception) {
                println("Error fetching weather: ${e.message}")
            }
        }
    }

    fun fetchWeatherByCity(cityName: String, startDate: String, endDate: String) {
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
    }
}