package com.hsact.sunplanner.ui.mainscreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.data.network.OpenMeteoGeo
import com.hsact.sunplanner.data.network.OpenMeteoService
import com.hsact.sunplanner.domain.usecase.AggregateWeatherByDateUseCase
import com.hsact.sunplanner.domain.usecase.CreateWeatherGraphBarsUseCase
import com.hsact.sunplanner.domain.usecase.CreateWeatherGraphLineUseCase
import com.hsact.sunplanner.domain.usecase.FetchFilteredWeatherUseCase
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val fetchFilteredWeatherUseCase: FetchFilteredWeatherUseCase,
    private val aggregateWeatherByDateUseCase: AggregateWeatherByDateUseCase,
    private val createWeatherGraphLineUseCase: CreateWeatherGraphLineUseCase,
    private val createWeatherGraphBarsUseCase: CreateWeatherGraphBarsUseCase
) : ViewModel() {

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

    fun onSearchClick () {
        if (_searchDataUI.value.location == null) {
            updateError("Location is empty")
            return
        }
        if (_searchDataUI.value.startLD > _searchDataUI.value.endLD) {
            updateError("Invalid date range")
            return
        }
        if (_searchDataUI.value.endLD.year - _searchDataUI.value.startLD.year > 20) {
            updateError("Years range is too big (max 20)")
            return
        }
        val params = prepareParamsForRequest()
        if (params != null) {
            fetchWeather(params)
        }
    }


    fun prepareParamsForRequest(): WeatherRequestParams? {
        val location = _searchDataUI.value.location?: return null
        val startDate = _searchDataUI.value.startLD
        val endDate = _searchDataUI.value.endLD

        return WeatherRequestParams().apply {
            latitude = location.latitude
            longitude = location.longitude
            this.startDate = startDate.toString() // YYYY-MM-DD
            this.endDate = endDate.toString()
        }
        //fetchWeather(params)
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
        _searchDataUI.value = _searchDataUI.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val filteredWeather = fetchFilteredWeatherUseCase.execute(
                    params,
                    _searchDataUI.value.startLD,
                    _searchDataUI.value.endLD
                )
                saveWeatherData(filteredWeather)
            } catch (e: Exception) {
                updateError("Error fetching weather: ${e.message}")
            }
            _searchDataUI.value = _searchDataUI.value.copy(isLoading = false)
        }
    }
    fun saveWeatherData(data: WeatherResponse)
    {
        _searchDataUI.value = _searchDataUI.value.copy(weatherData = data)
        var maxTemps = _searchDataUI.value.weatherData!!.daily.maxTemperature
        var minTemps = _searchDataUI.value.weatherData!!.daily.minTemperature
        var sunshine = _searchDataUI.value.weatherData!!.daily.sunshineDuration
            .map { ((it / 3600.0) * 10).roundToInt() / 10.0 }
        var precipitation = _searchDataUI.value.weatherData!!.daily.precipitationSum

        if (_searchDataUI.value.startLD.dayOfMonth != _searchDataUI.value.endLD.dayOfMonth ||
            _searchDataUI.value.startLD.monthValue != _searchDataUI.value.endLD.monthValue) {
            _searchDataUI.value = _searchDataUI.value.copy(isOneDay = false)
            val aggregated = aggregateWeatherByDateUseCase.execute(data.daily)
            maxTemps = aggregated.map { it.avgMaxTemp }
            minTemps = aggregated.map { it.avgMinTemp }
            sunshine = aggregated.map { (it.avgSunshineSeconds / 3600.0 * 10).roundToInt() / 10.0 }
            precipitation = aggregated.map { it.avgPrecipitation }
        }
        else {
            _searchDataUI.value = _searchDataUI.value.copy(isOneDay = true)
        }
        searchDataUI.value.maxTemperature =
            createWeatherGraphLineUseCase.invoke("Max", maxTemps, Color(0xFFFF5555))
        searchDataUI.value.minTemperature =
            createWeatherGraphLineUseCase.invoke("Min", minTemps, Color(0xFF4646FF))
        searchDataUI.value.sunDuration =
            createWeatherGraphLineUseCase.invoke("", sunshine, Color(0xFFFFFF50))
        searchDataUI.value.precipitation =
            createWeatherGraphBarsUseCase.invoke("", precipitation, Color(0xFF5555FF))
    }
}