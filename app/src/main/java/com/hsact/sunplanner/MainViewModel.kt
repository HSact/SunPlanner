package com.hsact.sunplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = WeatherRepository(RetrofitInstance.api)

    fun fetchWeather() {
        viewModelScope.launch {
            try {
                val response = repository.getWeather(
                    latitude = 55.7522,
                    longitude = 37.6156,
                    startDate = "2024-01-01",
                    endDate = "2024-01-02"
                )
                println(response)
            } catch (e: Exception) {
                println("Ошибка: ${e.message}")
            }
        }
    }
}
