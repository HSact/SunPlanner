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
import com.hsact.sunplanner.domain.model.WeatherGraphData
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLocationUseCase
import com.hsact.sunplanner.ui.settings.modes.nameToLanguageMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.toName
import com.hsact.sunplanner.ui.theme.avgTempLineColor
import com.hsact.sunplanner.ui.theme.daylightLineColor
import com.hsact.sunplanner.ui.theme.maxTempLineColor
import com.hsact.sunplanner.ui.theme.minTempLineColor
import com.hsact.sunplanner.ui.theme.precipitationBarColor
import com.hsact.sunplanner.ui.theme.sunShineLineColor
import com.hsact.sunplanner.ui.theme.windGustsSpeedColor
import com.hsact.sunplanner.ui.theme.windSpeedColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun handleIntent(intent: MainScreenIntents) {
        viewModelScope.launch {
            when (intent) {
                is MainScreenIntents.FetchCityList -> {
                    fetchCityList(intent.query)
                }

                is MainScreenIntents.UpdateLocation -> {
                    updateLocation(intent.city)
                }

                is MainScreenIntents.UpdateStartYear -> {
                    updateStartYear(intent.year)
                }

                is MainScreenIntents.UpdateStartMonth -> {
                    updateStartMonth(intent.month)
                }

                is MainScreenIntents.UpdateStartDay -> {
                    updateStartDay(intent.day)
                }

                is MainScreenIntents.UpdateEndYear -> {
                    updateEndYear(intent.year)
                }

                is MainScreenIntents.UpdateEndMonth -> {
                    updateEndMonth(intent.month)
                }

                is MainScreenIntents.UpdateEndDay -> {
                    updateEndDay(intent.day)
                }

                is MainScreenIntents.CleanError -> {
                    cleanError()
                }

                is MainScreenIntents.WeatherSearchClick -> {
                    onWeatherSearchClick()
                }
            }
        }
    }

    private fun updateLocation(city: Location) {
        viewModelScope.launch {
            updateLocationUseCase.invoke(city)
        }
    }

    private fun updateStartYear(year: Int) {
        val old = _mainUiState.value.startLD
        val newDate = old.withYear(year).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    private fun updateStartMonth(month: Int) {
        val old = _mainUiState.value.startLD
        val newDate = old.withMonth(month).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    private fun updateStartDay(day: Int) {
        val old = _mainUiState.value.startLD
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    private fun updateEndYear(year: Int) {
        val old = _mainUiState.value.endLD
        val newDate = old.withYear(year).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(endLD = newDate)
    }

    private fun updateEndMonth(month: Int) {
        val old = _mainUiState.value.endLD
        val newDate = old.withMonth(month).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(endLD = newDate)
    }

    private fun updateEndDay(day: Int) {
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

    private fun updateError(error: String) {
        _mainUiState.value = _mainUiState.value.copy(error = error)
    }

    private fun cleanError() {
        _mainUiState.value = _mainUiState.value.copy(error = "")
    }

    private fun updateConfirmedLD(start: LocalDate, end: LocalDate) {
        _mainUiState.value =
            _mainUiState.value.copy(confirmedStartLD = start, confirmedEndLD = end)
    }

    private suspend fun onWeatherSearchClick() {
        val state = _mainUiState.value
        if (!state.isLocationNotNull()) {
            updateError(stringProvider.locationEmpty())
            return
        }
        if (!state.isStartYearNotAfterEndYear()) {
            updateError(stringProvider.invalidYearRange())
            return
        }
        if (!state.isDateRangeValid()) {
            updateError(stringProvider.invalidDateRange())
            return
        }
        if (!state.isYearsRangeWithinLimit()) {
            updateError(stringProvider.yearsRangeTooBig(state.maxYearRange))
            return
        }
        val params = withContext(Dispatchers.Default) {
            prepareParamsForRequest(state)
        }
        if (params != null) {
            updateConfirmedLD(state.startLD, state.endLD)
            fetchWeather(params)
        }
    }

    private fun prepareParamsForRequest(state: MainUIState): WeatherRequestParams? {
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

    private fun fetchCityList(cityName: String) {
        viewModelScope.launch {
            try {
                var cities = repository.getCitiesList(
                    cityName = cityName,
                    language = _mainUiState.value.settingsBundle.languageMode.toName(),
                )
                if (cities != null) {
                    _mainUiState.value = _mainUiState.value.copy(cities = cities)
                }
            } catch (e: Exception) {
                updateError(stringProvider.fetchCitiesError(e))
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
                updateWeatherState(filteredWeather)
            } catch (e: Exception) {
                updateError(stringProvider.fetchWeatherError(e))
            }
            _mainUiState.value = _mainUiState.value.copy(isLoading = false)
        }
    }

    private suspend fun updateWeatherState(data: WeatherResponse) {
        val state = withContext(Dispatchers.Default) {
            buildWeatherDataState(_mainUiState.value, data)
        }
        _mainUiState.update { current ->
            state
        }
    }

    private fun buildWeatherDataState(
        state: MainUIState,
        data: WeatherResponse,
    ): MainUIState {
        var state = state
        val daily = data.daily
        state = state.copy(weatherData = data)
        val weatherMetrics = WeatherMetrics()
        weatherMetrics.maxTemps = daily.maxTemperature
        weatherMetrics.minTemps = daily.minTemperature
        weatherMetrics.averageTemps = weatherMetrics.maxTemps.indices.map { i ->
            val avg = (weatherMetrics.maxTemps[i] + weatherMetrics.minTemps[i]) / 2
            round(avg * 10) / 10
        }

        weatherMetrics.sunshine = daily.sunshineDuration
            .map { ((it / 3600.0) * 10).roundToInt() / 10.0 }
        weatherMetrics.dayLight = daily.daylightDuration
            .map { ((it / 3600.0) * 10).roundToInt() / 10.0 }
        weatherMetrics.precipitation = daily.precipitationSum
        weatherMetrics.windSpeed = daily.windSpeedMax
        weatherMetrics.gustSpeed = daily.windGustsMax

        state = state.copy(isOneYear = state.startLD.year == state.endLD.year)

        if (state.startLD.dayOfMonth != state.endLD.dayOfMonth ||
            state.startLD.monthValue != state.endLD.monthValue
        ) {
            state = state.copy(isOneDay = false)
            val aggregated = aggregateWeatherByDateUseCase.execute(data.daily)
            weatherMetrics.maxTemps = aggregated.map { it.avgMaxTemp }
            weatherMetrics.averageTemps = aggregated.map { it.avgAvgTemp }
            weatherMetrics.minTemps = aggregated.map { it.avgMinTemp }
            weatherMetrics.sunshine =
                aggregated.map { (it.avgSunshineSeconds / 3600.0 * 10).roundToInt() / 10.0 }
            weatherMetrics.dayLight =
                aggregated.map { (it.avgDaylightSeconds / 3600.0 * 10).roundToInt() / 10.0 }
            weatherMetrics.precipitation = aggregated.map { it.avgPrecipitation }
            weatherMetrics.windSpeed = aggregated.map { it.avgWindSpeed }
            weatherMetrics.gustSpeed = aggregated.map { it.avgWindGustSpeed }
        } else {
            state = state.copy(isOneDay = true)
            weatherMetrics.dayLight = weatherMetrics.dayLight.map { weatherMetrics.dayLight.average() }.toList()
        }
        val popUpLabels = DateUtils.generatePopUpLabels(
            state.startLD, state.endLD,
            state.settingsBundle.languageMode.toLocale()
        )
        val graphData = createGraphData(
            weatherMetrics,
            popUpLabels
        )
        state = state.copy(weatherGraphData = graphData)
        return state
    }

    private fun createGraphData(
        weatherMetrics: WeatherMetrics,
        popUpLabels: List<String>
    ): WeatherGraphData {
        val graphData = WeatherGraphData()
        graphData.maxTemperature =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.max(),
                values = weatherMetrics.maxTemps,
                dates = popUpLabels,
                isDotsVisible = _mainUiState.value.settingsBundle.isDotsVisible,
                isEdgesCurved = _mainUiState.value.settingsBundle.isEdgesCurved,
                color = maxTempLineColor,
                tintOpacity = 0.4F,
                isOneYear = _mainUiState.value.isOneYear
            )
        graphData.avgTemperature =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.avg(),
                values = weatherMetrics.averageTemps,
                dates = popUpLabels,
                isDotsVisible = _mainUiState.value.settingsBundle.isDotsVisible,
                isEdgesCurved = _mainUiState.value.settingsBundle.isEdgesCurved,
                color = avgTempLineColor,
                tintOpacity = 0.0F,
                isOneYear = _mainUiState.value.isOneYear
            )

        graphData.minTemperature =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.min(),
                values = weatherMetrics.minTemps,
                dates = popUpLabels,
                isDotsVisible = _mainUiState.value.settingsBundle.isDotsVisible,
                isEdgesCurved = _mainUiState.value.settingsBundle.isEdgesCurved,
                color = minTempLineColor,
                tintOpacity = 0.4F,
                isOneYear = _mainUiState.value.isOneYear
            )

        graphData.sunShineDuration =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.sunshine(),
                values = weatherMetrics.sunshine,
                dates = popUpLabels,
                isDotsVisible = _mainUiState.value.settingsBundle.isDotsVisible,
                isEdgesCurved = _mainUiState.value.settingsBundle.isEdgesCurved,
                color = sunShineLineColor,
                tintOpacity = 0.8F,
                isOneYear = _mainUiState.value.isOneYear
            )

        graphData.dayLightDuration =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.daylight(),
                values = weatherMetrics.dayLight,
                dates = popUpLabels,
                isDotsVisible = false, //_mainUiState.value.settingsBundle.isDotsVisible,
                isEdgesCurved = false, //_mainUiState.value.settingsBundle.isEdgesCurved,
                color = daylightLineColor,
                tintOpacity = 0.0F,
                isOneYear = _mainUiState.value.isOneYear
            )

        graphData.precipitation =
            createWeatherGraphBarsUseCase.invoke("", weatherMetrics.precipitation, precipitationBarColor)

        graphData.windSpeed =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.wind(),
                values = weatherMetrics.windSpeed,
                dates = popUpLabels,
                isDotsVisible = _mainUiState.value.settingsBundle.isDotsVisible,
                isEdgesCurved = _mainUiState.value.settingsBundle.isEdgesCurved,
                color = windSpeedColor,
                tintOpacity = 0.5F,
                isOneYear = _mainUiState.value.isOneYear
            )

        graphData.windGustsSpeed =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.gusts(),
                values = weatherMetrics.gustSpeed,
                dates = popUpLabels,
                isDotsVisible = _mainUiState.value.settingsBundle.isDotsVisible,
                isEdgesCurved = _mainUiState.value.settingsBundle.isEdgesCurved,
                color = windGustsSpeedColor,
                tintOpacity = 0.5F,
                isOneYear = _mainUiState.value.isOneYear
            )
        return graphData
    }
}