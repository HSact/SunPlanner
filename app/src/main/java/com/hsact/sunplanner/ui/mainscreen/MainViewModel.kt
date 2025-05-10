package com.hsact.sunplanner.ui.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.repository.WeatherRepository
import com.hsact.sunplanner.domain.usecase.weather.AggregateWeatherByDateUseCase
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphBarsUseCase
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphLineUseCase
import com.hsact.sunplanner.domain.usecase.weather.FetchFilteredWeatherUseCase
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.data.utils.StringProvider
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLocationUseCase
import com.hsact.sunplanner.ui.settings.modes.nameToLanguageMode
import com.hsact.sunplanner.ui.settings.modes.toLocale
import com.hsact.sunplanner.ui.settings.modes.toName
import com.hsact.sunplanner.ui.settings.modes.unitModes.toName
import com.hsact.sunplanner.ui.theme.avgTempLineColor
import com.hsact.sunplanner.ui.theme.maxTempLineColor
import com.hsact.sunplanner.ui.theme.minTempLineColor
import com.hsact.sunplanner.ui.theme.precipitationBarColor
import com.hsact.sunplanner.ui.theme.sunShineLineColor
import com.hsact.sunplanner.ui.theme.windGustsSpeedColor
import com.hsact.sunplanner.ui.theme.windSpeedColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.roundToInt

@FlowPreview
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val stringProvider: StringProvider,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val fetchFilteredWeatherUseCase: FetchFilteredWeatherUseCase,
    private val aggregateWeatherByDateUseCase: AggregateWeatherByDateUseCase,
    private val createWeatherGraphLineUseCase: CreateWeatherGraphLineUseCase,
    private val createWeatherGraphBarsUseCase: CreateWeatherGraphBarsUseCase
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUIState())
    val mainUiState: StateFlow<MainUIState> get() = _mainUiState

    init {
        viewModelScope.launch {
            combine(
                getSettingsUseCase.location,
                getSettingsUseCase.isDotsVisible,
                getSettingsUseCase.isEdgesCurved
            ) { location, isDotsVisible, isEdgesCurved ->
                _mainUiState.value.copy(
                    settingsBundle = _mainUiState.value.settingsBundle.copy(
                        location = location,
                        isDotsVisible = isDotsVisible,
                        isEdgesCurved = isEdgesCurved
                    )
                )
            }.collect { updatedUiState ->
                _mainUiState.value = updatedUiState
            }
        }
        viewModelScope.launch {
            combine(
                getSettingsUseCase.language,
                getSettingsUseCase.theme,
                getSettingsUseCase.temperatureUnit,
                getSettingsUseCase.windUnit,
                getSettingsUseCase.precipitationUnit
            ) { language, theme, tempUnit, windUnit, precipitationUnit ->
                SettingsBundle(
                    location = _mainUiState.value.settingsBundle.location,
                    isDotsVisible = _mainUiState.value.settingsBundle.isDotsVisible,
                    isEdgesCurved = _mainUiState.value.settingsBundle.isEdgesCurved,
                    languageMode = language ?: nameToLanguageMode(Locale.getDefault().language),
                    themeMode = theme,
                    temperatureUnitMode = tempUnit,
                    windUnitMode = windUnit,
                    precipitationUnitMode = precipitationUnit
                )
            }.debounce(200).collect { updatedBundle ->
                _mainUiState.value = _mainUiState.value.copy(settingsBundle = updatedBundle)
                if (_mainUiState.value.weatherData != null && !_mainUiState.value.isLoading) {
                    fetchWeather(prepareParamsForRequest(_mainUiState.value) ?: return@collect)
                }
            }
        }
    }

    fun updateLocation(city: Location) {
        //_searchDataUI.value = _searchDataUI.value.copy(location = city)
        viewModelScope.launch {
            updateLocationUseCase.invoke(city)
        }
    }

    fun updateStartYear(year: Int) {
        val old = _mainUiState.value.startLD
        val newDate = old.withYear(year).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    fun updateStartMonth(month: Int) {
        val old = _mainUiState.value.startLD
        val newDate = old.withMonth(month).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    fun updateStartDay(day: Int) {
        val old = _mainUiState.value.startLD
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    fun updateEndYear(year: Int) {
        val old = _mainUiState.value.endLD
        val newDate = old.withYear(year).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(endLD = newDate)
    }

    fun updateEndMonth(month: Int) {
        val old = _mainUiState.value.endLD
        val newDate = old.withMonth(month).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(endLD = newDate)
    }

    fun updateEndDay(day: Int) {
        val old = _mainUiState.value.endLD
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _mainUiState.value = _mainUiState.value.copy(endLD = newDate)
    }

    private fun LocalDate.coerceDay(): LocalDate {
        val maxDay = this.lengthOfMonth()
        return if (this.dayOfMonth > maxDay) this.withDayOfMonth(maxDay) else this
    }

    fun updateError(error: String) {
        _mainUiState.value = _mainUiState.value.copy(error = error)
    }

    fun cleanError() {
        _mainUiState.value = _mainUiState.value.copy(error = "")
    }

    fun updateConfirmedLD(start: LocalDate, end: LocalDate) {
        _mainUiState.value =
            _mainUiState.value.copy(confirmedStartLD = start, confirmedEndLD = end)
    }

    fun onWeatherSearchClick() {
        if (_mainUiState.value.settingsBundle.location == null) {
            updateError(stringProvider.locationEmpty())
            return
        }
        if (_mainUiState.value.startLD.year > _mainUiState.value.endLD.year) {
            updateError(stringProvider.invalidYearRange())
            return
        }
        if (_mainUiState.value.startLD.withYear(_mainUiState.value.endLD.year).dayOfYear >
            _mainUiState.value.endLD.dayOfYear
        ) {
            updateError(stringProvider.invalidDateRange())
            return
        }
        if (_mainUiState.value.endLD.year - _mainUiState.value.startLD.year >= 20) {
            //updateError("Years range is too big (max 20)")
            updateError(stringProvider.yearsRangeTooBig())
            return
        }
        val params = prepareParamsForRequest(_mainUiState.value)
        if (params != null) {
            updateConfirmedLD(_mainUiState.value.startLD, _mainUiState.value.endLD)
            fetchWeather(params)
        }
    }

    fun prepareParamsForRequest(state: MainUIState): WeatherRequestParams? {
        val location = state.settingsBundle.location ?: return null
        val startDate = state.startLD
        val endDate = state.endLD
        val temperatureUnit = state.settingsBundle.temperatureUnitMode.toName()
        val windSpeedUnit = state.settingsBundle.windUnitMode.toName()
        val precipitationUnit = state.settingsBundle.precipitationUnitMode.toName()

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
                    language = _mainUiState.value.settingsBundle.languageMode.toName(),
                )
            } catch (e: Exception) {
                //updateError("Error fetching cities: ${e.message}")
                updateError(stringProvider.fetchCitiesError(e))
            }
            if (cities != null) {
                _mainUiState.value = _mainUiState.value.copy(cities = cities!!)
            }
        }
    }

    private fun fetchWeather(params: WeatherRequestParams) {
        _mainUiState.value = _mainUiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val filteredWeather = fetchFilteredWeatherUseCase.execute(
                    params,
                    _mainUiState.value.startLD,
                    _mainUiState.value.endLD
                )
                processWeatherData(filteredWeather)
            } catch (e: Exception) {
                updateError(stringProvider.fetchWeatherError(e))
            }
            _mainUiState.value = _mainUiState.value.copy(isLoading = false)
        }
    }

    fun processWeatherData(data: WeatherResponse) {
        _mainUiState.value = _mainUiState.value.copy(weatherData = data)
        var maxTemps = _mainUiState.value.weatherData!!.daily.maxTemperature
        var minTemps = _mainUiState.value.weatherData!!.daily.minTemperature
        var averageTemps = maxTemps.indices.map { i ->
            val avg = (maxTemps[i] + minTemps[i]) / 2
            round(avg * 10) / 10
        }
        var sunshine = _mainUiState.value.weatherData!!.daily.sunshineDuration
            .map { ((it / 3600.0) * 10).roundToInt() / 10.0 }
        var precipitation = _mainUiState.value.weatherData!!.daily.precipitationSum
        var windSpeed = _mainUiState.value.weatherData!!.daily.windSpeedMax
        var gustSpeed = _mainUiState.value.weatherData!!.daily.windGustsMax

        if (_mainUiState.value.startLD.year == _mainUiState.value.endLD.year) {
            _mainUiState.value = _mainUiState.value.copy(isOneYear = true)
        } else {
            _mainUiState.value = _mainUiState.value.copy(isOneYear = false)
        }

        if (_mainUiState.value.startLD.dayOfMonth != _mainUiState.value.endLD.dayOfMonth ||
            _mainUiState.value.startLD.monthValue != _mainUiState.value.endLD.monthValue
        ) {
            _mainUiState.value = _mainUiState.value.copy(isOneDay = false)
            val aggregated = aggregateWeatherByDateUseCase.execute(data.daily)
            maxTemps = aggregated.map { it.avgMaxTemp }
            averageTemps = aggregated.map { it.avgAvgTemp }
            minTemps = aggregated.map { it.avgMinTemp }
            sunshine = aggregated.map { (it.avgSunshineSeconds / 3600.0 * 10).roundToInt() / 10.0 }
            precipitation = aggregated.map { it.avgPrecipitation }
            windSpeed = aggregated.map { it.avgWindSpeed }
            gustSpeed = aggregated.map { it.avgWindGustSpeed }
        } else {
            _mainUiState.value = _mainUiState.value.copy(isOneDay = true)
        }
        val popUpLabels = DateUtils.generatePopUpLabels(
            _mainUiState.value.startLD, _mainUiState.value.endLD,
            _mainUiState.value.settingsBundle.languageMode.toLocale()
        )
        _mainUiState.value.weatherGraphData.maxTemperature =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.max(),
                maxTemps,
                popUpLabels,
                _mainUiState.value.settingsBundle.isDotsVisible,
                _mainUiState.value.settingsBundle.isEdgesCurved,
                maxTempLineColor,
                _mainUiState.value.isOneYear
            )
        _mainUiState.value.weatherGraphData.avgTemperature =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.avg(),
                averageTemps,
                popUpLabels,
                _mainUiState.value.settingsBundle.isDotsVisible,
                _mainUiState.value.settingsBundle.isEdgesCurved,
                avgTempLineColor,
                _mainUiState.value.isOneYear
            )

        _mainUiState.value.weatherGraphData.minTemperature =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.min(),
                minTemps,
                popUpLabels,
                _mainUiState.value.settingsBundle.isDotsVisible,
                _mainUiState.value.settingsBundle.isEdgesCurved,
                minTempLineColor,
                _mainUiState.value.isOneYear
            )

        _mainUiState.value.weatherGraphData.sunDuration =
            createWeatherGraphLineUseCase.invoke(
                "",
                sunshine,
                popUpLabels,
                _mainUiState.value.settingsBundle.isDotsVisible,
                _mainUiState.value.settingsBundle.isEdgesCurved,
                sunShineLineColor,
                _mainUiState.value.isOneYear
            )

        _mainUiState.value.weatherGraphData.precipitation =
            createWeatherGraphBarsUseCase.invoke("", precipitation, precipitationBarColor)

        _mainUiState.value.weatherGraphData.windSpeed =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.wind(),
                windSpeed,
                popUpLabels,
                _mainUiState.value.settingsBundle.isDotsVisible,
                _mainUiState.value.settingsBundle.isEdgesCurved,
                windSpeedColor,
                _mainUiState.value.isOneYear
            )

        _mainUiState.value.weatherGraphData.windGustsSpeed =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.gusts(),
                gustSpeed,
                popUpLabels,
                _mainUiState.value.settingsBundle.isDotsVisible,
                _mainUiState.value.settingsBundle.isEdgesCurved,
                windGustsSpeedColor,
                _mainUiState.value.isOneYear
            )
    }
}