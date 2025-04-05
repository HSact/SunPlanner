package com.hsact.sunplanner.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.LocationUtils
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.network.RetrofitInstance
import com.hsact.sunplanner.network.WeatherRequestParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository =
        WeatherRepository(RetrofitInstance.WeatherApi, RetrofitInstance.GeolocationApi)

    private val _searchDataUI = MutableStateFlow(MainUIData())
    val searchDataUI: StateFlow<MainUIData> get() = _searchDataUI

    fun saveLocationToVM(city: Location) {
        _searchDataUI.value = _searchDataUI.value.copy(location = city)
    }

    fun prepareParamsForRequest() {
        val params = WeatherRequestParams()
        params.latitude = _searchDataUI.value.location?.latitude ?: 0.0
        params.longitude = _searchDataUI.value.location?.longitude ?: 0.0
        params.startDate = _searchDataUI.value.startYear.toString() + _searchDataUI.value.startDate
        //params.startDate = LocationUtils.prepareDate(_searchDataUI.value.startYear, _searchDataUI.value.startDate)
        params.endDate = _searchDataUI.value.endDate.toString() + _searchDataUI.value.endDate
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
                //_uiState.value = "Город не найден"
                println("Error fetching coordinates")
            }
        }
    }
}