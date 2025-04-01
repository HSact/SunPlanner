package com.hsact.sunplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = WeatherRepository(RetrofitInstance.WeatherApi, RetrofitInstance.GeolocationApi)

    fun fetchWeather(latitude: Double, longitude: Double, startDate: String, endDate: String) {
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
                println("Ошибка: ${e.message}")
            }
        }
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
                println("Ошибка geo")
            }
        }
    }
}