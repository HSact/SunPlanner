package com.hsact.sunplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.Location
import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = WeatherRepository(RetrofitInstance.WeatherApi, RetrofitInstance.GeolocationApi)

    private val _searchDataUI = MutableStateFlow(MainUIData())
    val searchDataUI: StateFlow<MainUIData> get() = _searchDataUI

    fun fetchCityList(cityName: String): List<Location>? {
        var cities: List<Location>? = null
        viewModelScope.launch {
            cities = repository.getCitiesList(
                cityName = cityName
            )
            if (cities != null) {
                _searchDataUI.value.cities = cities!!
            println(cities)
            }
        }
        return cities
    }

    fun fetchWeatherByCity(cityName: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val location = repository.getCoordinatesByCity(
                cityName = cityName
            )
            if (location != null) {
                fetchWeather(location.latitude, location.longitude, startDate, endDate)
            } else {
                //_uiState.value = "Город не найден"
                println("Error fetching coordinates")
            }
        }
    }
    private fun fetchWeather(latitude: Double, longitude: Double, startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                val response = repository.getWeather(
                    latitude = latitude,
                    longitude = longitude,
                    startDate = startDate,
                    endDate = endDate
                )
                println(response)
            } catch (e: Exception) {
                println("Error fetching weather: ${e.message}")
            }
        }
    }
}