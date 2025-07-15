package com.hsact.sunplanner.ui.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.error.toApiError
import com.hsact.sunplanner.domain.factory.WeatherAvgValuesFactory
import com.hsact.sunplanner.domain.model.DatesBundle
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.repository.StringProvider
import com.hsact.sunplanner.domain.repository.WeatherRepository
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLocationUseCase
import com.hsact.sunplanner.domain.usecase.weather.FetchFilteredWeatherUseCase
import com.hsact.sunplanner.ui.settings.modes.nameToLanguageMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.toName
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
import java.util.UUID
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
    private val weatherAvgValuesFactory: WeatherAvgValuesFactory,
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
                    fetchWeather(
                        prepareParamsForRequest(
                            _mainUiState.value.settingsBundle,
                            _mainUiState.value.confirmedDates
                        ) ?: return@collect
                    )
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

                is MainScreenIntents.CleanValidationError -> {
                    cleanValidationError()
                }
                is MainScreenIntents.CleanNetworkError -> {
                    cleanNetworkError()
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
        val old = _mainUiState.value.tempDates.startDate
        val newDate = old.withYear(year).coerceDay()
        _mainUiState.value =
            _mainUiState.value.copy(tempDates = _mainUiState.value.tempDates.copy(startDate = newDate))
    }

    private fun updateStartMonth(month: Int) {
        val old = _mainUiState.value.tempDates.startDate
        val newDate = old.withMonth(month).coerceDay()
        _mainUiState.value =
            _mainUiState.value.copy(tempDates = _mainUiState.value.tempDates.copy(startDate = newDate))
    }

    private fun updateStartDay(day: Int) {
        val old = _mainUiState.value.tempDates.startDate
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _mainUiState.value =
            _mainUiState.value.copy(tempDates = _mainUiState.value.tempDates.copy(startDate = newDate))
    }

    private fun updateEndYear(year: Int) {
        val old = _mainUiState.value.tempDates.endDate
        val newDate = old.withYear(year).coerceDay()
        _mainUiState.value =
            _mainUiState.value.copy(tempDates = _mainUiState.value.tempDates.copy(endDate = newDate))
    }

    private fun updateEndMonth(month: Int) {
        val old = _mainUiState.value.tempDates.endDate
        val newDate = old.withMonth(month).coerceDay()
        _mainUiState.value =
            _mainUiState.value.copy(tempDates = _mainUiState.value.tempDates.copy(endDate = newDate))
    }

    private fun updateEndDay(day: Int) {
        val old = _mainUiState.value.tempDates.endDate
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _mainUiState.value =
            _mainUiState.value.copy(tempDates = _mainUiState.value.tempDates.copy(endDate = newDate))
    }

    private fun LocalDate.coerceDay(): LocalDate {
        val maxDay = this.lengthOfMonth()
        return if (this.dayOfMonth > maxDay) this.withDayOfMonth(maxDay) else this
    }

    private fun setValidationError(error: String) {
        _mainUiState.value = _mainUiState.value.copy(validationError = error)
    }

    private fun setNetworkError(error: ApiError) {
        _mainUiState.value = _mainUiState.value.copy(
            networkError = error,
            networkErrorId = UUID.randomUUID().toString())
    }

    private fun cleanValidationError() {
        _mainUiState.value = _mainUiState.value.copy(validationError = null)
    }

    private fun cleanNetworkError() {
        _mainUiState.value = _mainUiState.value.copy(networkError = null)
    }
    private fun updateConfirmedDates(dates: DatesBundle) {
        _mainUiState.value =
            _mainUiState.value.copy(
                confirmedDates = _mainUiState.value.confirmedDates.copy(
                    startDate = dates.startDate,
                    endDate = dates.endDate
                )
            )
    }

    private suspend fun onWeatherSearchClick() {
        val state = _mainUiState.value
        if (!state.isLocationNotNull()) {
            setValidationError(stringProvider.locationEmpty())
            return
        }
        if (!state.tempDates.isStartYearNotAfterEndYear()) {
            setValidationError(stringProvider.invalidYearRange())
            return
        }
        if (!state.tempDates.isDateRangeValid()) {
            setValidationError(stringProvider.invalidDateRange())
            return
        }
        if (!state.tempDates.isYearsRangeWithinLimit(state.maxYearRange)) {
            setValidationError(stringProvider.yearsRangeTooBig(state.maxYearRange))
            return
        }
        val params = withContext(Dispatchers.Default) {
            prepareParamsForRequest(state.settingsBundle, state.tempDates)
        }
        if (params != null) {
            updateConfirmedDates(state.tempDates)
            fetchWeather(params)
        }
    }

    private fun prepareParamsForRequest(
        settingsBundle: SettingsBundle,
        dates: DatesBundle
    ): WeatherRequestParams? {
        val location = settingsBundle.location ?: return null
        val startDate = dates.startDate
        val endDate = dates.endDate
        val temperatureUnit = settingsBundle.temperatureUnitMode.toName()
        val windSpeedUnit = settingsBundle.windUnitMode.toName()
        val precipitationUnit = settingsBundle.precipitationUnitMode.toName()

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
                val cities = repository.getCitiesList(
                    cityName = cityName,
                    language = _mainUiState.value.settingsBundle.languageMode.toName(),
                )
                if (cities != null) {
                    _mainUiState.value = _mainUiState.value.copy(cities = cities)
                }
            } catch (e: Exception) {
                setNetworkError(e.toApiError())
            }
        }
    }

    private fun fetchWeather(params: WeatherRequestParams) {
        _mainUiState.value = _mainUiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val filteredWeather = fetchFilteredWeatherUseCase.execute(
                    params,
                    _mainUiState.value.tempDates.startDate,
                    _mainUiState.value.tempDates.endDate
                )
                updateWeatherState(filteredWeather)
            } catch (e: Exception) {
                setNetworkError(e.toApiError())
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
        state = state.copy(
            weatherData = data,
            isOneYear = state.tempDates.startDate.year == state.tempDates.endDate.year
        )

        state = if (state.tempDates.startDate.dayOfMonth != state.tempDates.endDate.dayOfMonth ||
            state.tempDates.startDate.monthValue != state.tempDates.endDate.monthValue
        ) {
            state.copy(isOneDay = false)
        } else {
            state.copy(isOneDay = true)
        }
        val weatherMetrics = createWeatherMetrics(data, state.isOneDay)
        state = state.copy(weatherMetrics = weatherMetrics)
        return state
    }

    private fun createWeatherMetrics(data: WeatherResponse, isOneDay: Boolean): WeatherMetrics {
        val daily = data.daily
        var weatherMetrics = WeatherMetrics()
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
        if (isOneDay) {
            weatherMetrics.dayLight =
                weatherMetrics.dayLight.map { weatherMetrics.dayLight.average() }.toList()
        } else {
            weatherMetrics = weatherAvgValuesFactory.create(data, weatherMetrics)
        }
        return weatherMetrics
    }
}