package com.hsact.sunplanner.ui.mainscreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.domain.usecase.AggregateWeatherByDateUseCase
import com.hsact.sunplanner.domain.usecase.CreateWeatherGraphBarsUseCase
import com.hsact.sunplanner.domain.usecase.CreateWeatherGraphLineUseCase
import com.hsact.sunplanner.domain.usecase.FetchFilteredWeatherUseCase
import com.hsact.sunplanner.data.network.RetrofitInstance
import com.hsact.sunplanner.data.network.WeatherRequestParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

class MainViewModel() : ViewModel() {
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

    private fun LocalDate.coerceDay(): LocalDate {
        val maxDay = this.lengthOfMonth()
        return if (this.dayOfMonth > maxDay) this.withDayOfMonth(maxDay) else this
    }

    /*private fun prepareDate(month: String, day: Int): String {
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
    }*/
    fun updateError(error: String) {
        _searchDataUI.value = _searchDataUI.value.copy(error = error)
    }

    fun cleanError() {
        _searchDataUI.value = _searchDataUI.value.copy(error = "")
    }
    fun updateConfirmedLD(start: LocalDate, end: LocalDate) {
        _searchDataUI.value = _searchDataUI.value.copy(confirmedStartLD = start, confirmedEndLD = end)
    }


    fun prepareParamsForRequest() {
        val location = _searchDataUI.value.location
        val startDate = _searchDataUI.value.startLD
        val endDate = _searchDataUI.value.endLD

        if (location == null) {
            updateError("Location is empty")
            return
        }
        if (startDate > endDate) {
            updateError("Invalid date range")
            return
        }
        if (endDate.year - startDate.year > 20) {
            updateError("Years range is too big (max 20)")
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
            try {
                cities = repository.getCitiesList(
                    cityName = cityName
                )
            } catch (e: Exception) {
                updateError("Error fetching cities: ${e.message}")
            }
            if (cities != null) {
                _searchDataUI.value = _searchDataUI.value.copy(cities = cities!!)
                println(cities)
            }
        }
    }

    private fun fetchWeather(params: WeatherRequestParams) {
        updateConfirmedLD(_searchDataUI.value.startLD, _searchDataUI.value.endLD)
        viewModelScope.launch {
            try {
                val filteredWeather = FetchFilteredWeatherUseCase(repository).execute(
                    params,
                    _searchDataUI.value.startLD,
                    _searchDataUI.value.endLD
                )
                _searchDataUI.value = _searchDataUI.value.copy(weatherData = filteredWeather)
                var maxTemps = _searchDataUI.value.weatherData!!.daily.maxTemperature
                var minTemps = _searchDataUI.value.weatherData!!.daily.minTemperature
                var sunshine = _searchDataUI.value.weatherData!!.daily.sunshineDuration
                    .map { ((it / 3600.0) * 10).roundToInt() / 10.0 }
                var precipitation = _searchDataUI.value.weatherData!!.daily.precipitationSum

                if (_searchDataUI.value.startLD.dayOfMonth != _searchDataUI.value.endLD.dayOfMonth ||
                    _searchDataUI.value.startLD.monthValue != _searchDataUI.value.endLD.monthValue) {
                    _searchDataUI.value = _searchDataUI.value.copy(isOneDay = false)
                    val aggregated = AggregateWeatherByDateUseCase().execute(filteredWeather.daily)
                    maxTemps = aggregated.map { it.avgMaxTemp }
                    minTemps = aggregated.map { it.avgMinTemp }
                    sunshine = aggregated.map { (it.avgSunshineSeconds / 3600.0 * 10).roundToInt() / 10.0 }
                    precipitation = aggregated.map { it.avgPrecipitation }
                }
                else {
                    _searchDataUI.value = _searchDataUI.value.copy(isOneDay = true)
                }
                searchDataUI.value.maxTemperature =
                    CreateWeatherGraphLineUseCase().invoke("Max", maxTemps, Color(0xFFFF5555))
                searchDataUI.value.minTemperature =
                    CreateWeatherGraphLineUseCase().invoke("Min", minTemps, Color(0xFF4646FF))
                searchDataUI.value.sunDuration =
                    CreateWeatherGraphLineUseCase().invoke("", sunshine, Color(0xFFFFFF50))
                searchDataUI.value.precipitation =
                    CreateWeatherGraphBarsUseCase().invoke("", precipitation, Color(0xFF5555FF))
            } catch (e: Exception) {
                println("Error fetching weather: ${e.message}")
                updateError("Error fetching weather: ${e.message}")
            }
        }
    }
}