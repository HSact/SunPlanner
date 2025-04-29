package com.hsact.sunplanner.ui.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.repository.WeatherRepository
import com.hsact.sunplanner.domain.usecase.AggregateWeatherByDateUseCase
import com.hsact.sunplanner.domain.usecase.CreateWeatherGraphBarsUseCase
import com.hsact.sunplanner.domain.usecase.CreateWeatherGraphLineUseCase
import com.hsact.sunplanner.domain.usecase.FetchFilteredWeatherUseCase
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.data.utils.StringProvider
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.ThemeMode
import com.hsact.sunplanner.ui.settings.toName
import com.hsact.sunplanner.ui.theme.maxTempLineColor
import com.hsact.sunplanner.ui.theme.minTempLineColor
import com.hsact.sunplanner.ui.theme.precipitationBarColor
import com.hsact.sunplanner.ui.theme.sunShineLineColor
import com.hsact.sunplanner.ui.theme.windGustsSpeedColor
import com.hsact.sunplanner.ui.theme.windSpeedColor
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
    private val stringProvider: StringProvider,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val fetchFilteredWeatherUseCase: FetchFilteredWeatherUseCase,
    private val aggregateWeatherByDateUseCase: AggregateWeatherByDateUseCase,
    private val createWeatherGraphLineUseCase: CreateWeatherGraphLineUseCase,
    private val createWeatherGraphBarsUseCase: CreateWeatherGraphBarsUseCase
) : ViewModel() {

    private val _searchDataUI = MutableStateFlow(MainUIState())
    val searchDataUI: StateFlow<MainUIState> get() = _searchDataUI

    init {
        viewModelScope.launch {
            getSettingsUseCase.language.collect { language: LanguageMode ->
                _searchDataUI.value = _searchDataUI.value.copy(languageMode = language)
            }
        }
        viewModelScope.launch {
            getSettingsUseCase.theme.collect { theme: ThemeMode ->
                _searchDataUI.value = _searchDataUI.value.copy(themeMode = theme)
            }
        }
    }

    fun updateLocation(city: Location) {
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
            //updateError("Location is empty")
            updateError(stringProvider.locationEmpty())
            return
        }
        if (_searchDataUI.value.startLD > _searchDataUI.value.endLD) {
            //updateError("Invalid date range")
            updateError(stringProvider.invalidDateRange())
            return
        }
        if (_searchDataUI.value.endLD.year - _searchDataUI.value.startLD.year > 20) {
            //updateError("Years range is too big (max 20)")
            updateError(stringProvider.yearsRangeTooBig())
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
        val temperatureUnit = "celsius" //TODO: get from settings
        val windSpeedUnit = "ms"
        val precipitationUnit = "mm"

        return WeatherRequestParams().apply {
            latitude = location.latitude
            longitude = location.longitude
            this.startDate = startDate.toString() // YYYY-MM-DD
            this.endDate = endDate.toString()
            this.temperatureUnit = temperatureUnit
            this.windSpeedUnit = windSpeedUnit
            this.precipitationUnit = precipitationUnit
        }
    }

    fun fetchCityList(cityName: String) {
        var cities: List<Location>? = null
        viewModelScope.launch {
            try {
                cities = repository.getCitiesList(
                    cityName = cityName,
                    language = _searchDataUI.value.languageMode.toName(),
                )
            } catch (e: Exception) {
                //updateError("Error fetching cities: ${e.message}")
                updateError(stringProvider.fetchCitiesError(e))
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
                updateError(stringProvider.fetchWeatherError(e))
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

        if (_searchDataUI.value.startLD.year == _searchDataUI.value.endLD.year) {
            _searchDataUI.value = _searchDataUI.value.copy(isOneYear = true)
        }
        else {
            _searchDataUI.value = _searchDataUI.value.copy(isOneYear = false)
        }

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
        _searchDataUI.value.maxTemperature =
            createWeatherGraphLineUseCase.invoke(stringProvider.max(), maxTemps,
                maxTempLineColor, _searchDataUI.value.isOneYear)

        _searchDataUI.value.minTemperature =
            createWeatherGraphLineUseCase.invoke(stringProvider.min(), minTemps,
                minTempLineColor, _searchDataUI.value.isOneYear)

        _searchDataUI.value.sunDuration =
            createWeatherGraphLineUseCase.invoke("", sunshine,
                sunShineLineColor, _searchDataUI.value.isOneYear)

        _searchDataUI.value.precipitation =
            createWeatherGraphBarsUseCase.invoke("", precipitation, precipitationBarColor)

        _searchDataUI.value.windSpeed =
            createWeatherGraphLineUseCase.invoke(stringProvider.wind(), data.daily.windSpeedMax,
                windSpeedColor, _searchDataUI.value.isOneYear)

        _searchDataUI.value.windGustsSpeed =
            createWeatherGraphLineUseCase.invoke(stringProvider.gusts(), data.daily.windGustsMax,
                windGustsSpeedColor, _searchDataUI.value.isOneYear)
    }
}