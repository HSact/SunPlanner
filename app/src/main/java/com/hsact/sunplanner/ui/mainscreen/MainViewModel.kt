package com.hsact.sunplanner.ui.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.analytics.AnalyticsHelper
import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.error.toApiError
import com.hsact.sunplanner.domain.factory.WeatherMetricsFactory
import com.hsact.sunplanner.domain.model.DatesBundle
import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.repository.StringProvider
import com.hsact.sunplanner.domain.repository.WeatherRepository
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLocationUseCase
import com.hsact.sunplanner.domain.usecase.weather.FetchFilteredWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val stringProvider: StringProvider,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val fetchFilteredWeatherUseCase: FetchFilteredWeatherUseCase,
    private val weatherMetricsFactory: WeatherMetricsFactory,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUIState())
    val mainUiState: StateFlow<MainUIState> get() = _mainUiState.asStateFlow()

    init {
        analyticsHelper.logAppStarted()
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
                    languageMode = language ?: LanguageMode.fromName(Locale.getDefault().language),
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
        updateStartDate { it.withYear(year) }
    }

    private fun updateStartMonth(month: Int) {
        updateStartDate { it.withMonth(month) }
    }

    private fun updateStartDay(day: Int) {
        updateStartDate { it.withDayOfMonth(day.coerceIn(1, it.lengthOfMonth())) }
    }

    private fun updateEndYear(year: Int) {
        updateEndDate { it.withYear(year) }
    }

    private fun updateEndMonth(month: Int) {
        updateEndDate { it.withMonth(month) }
    }

    private fun updateEndDay(day: Int) {
        updateEndDate { it.withDayOfMonth(day.coerceIn(1, it.lengthOfMonth())) }
    }

    private fun updateStartDate(transform: (LocalDate) -> LocalDate) {
        _mainUiState.update { state ->
            val newDate = transform(state.tempDates.start).coerceDay()
            state.copy(tempDates = state.tempDates.copy(start = newDate))
        }
    }

    private fun updateEndDate(transform: (LocalDate) -> LocalDate) {
        _mainUiState.update { state ->
            val newDate = transform(state.tempDates.end).coerceDay()
            state.copy(tempDates = state.tempDates.copy(end = newDate))
        }
    }

    private fun LocalDate.coerceDay(): LocalDate {
        val maxDay = this.lengthOfMonth()
        return if (this.dayOfMonth > maxDay) this.withDayOfMonth(maxDay) else this
    }

    private fun setValidationError(error: String) {
        _mainUiState.update { it.copy(validationError = error) }
    }

    private fun setNetworkError(error: ApiError) {
        _mainUiState.update {
            it.copy(
                networkError = error,
                networkErrorId = UUID.randomUUID().toString()
            )
        }
    }

    private fun cleanValidationError() {
        _mainUiState.update { it.copy(validationError = null) }
    }

    private fun cleanNetworkError() {
        _mainUiState.update { it.copy(networkError = null) }
    }

    private fun updateConfirmedDates(dates: DatesBundle) {
        _mainUiState.update {
            it.copy(
                confirmedDates = it.confirmedDates.copy(
                    start = dates.start,
                    end = dates.end
                )
            )
        }
    }

    private suspend fun onWeatherSearchClick() {
        val state = _mainUiState.value
        if (!state.isLocationNotNull) {
            setValidationError(stringProvider.locationEmpty())
            return
        }
        if (!state.tempDates.isStartYearNotAfterEndYear) {
            setValidationError(stringProvider.invalidYearRange())
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
            analyticsHelper.logWeatherSearchClicked(
                location = state.settingsBundle.location?.name ?: "unknown",
                startDate = state.tempDates.start.toString(),
                endDate = state.tempDates.end.toString()
            )
            fetchWeather(params)
        }
    }

    private fun prepareParamsForRequest(
        settingsBundle: SettingsBundle,
        dates: DatesBundle
    ): WeatherRequestParams? {
        val location = settingsBundle.location ?: return null
        val startDate = dates.start
        val endDate = dates.end
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
                    _mainUiState.update { it.copy(cities = cities) }
                }
            } catch (e: Exception) {
                setNetworkError(e.toApiError())
            }
        }
    }

    private fun fetchWeather(params: WeatherRequestParams) {
        _mainUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val filteredWeather = fetchFilteredWeatherUseCase.execute(
                    params,
                    _mainUiState.value.tempDates.start,
                    _mainUiState.value.tempDates.end
                )
                updateWeatherState(filteredWeather)
                analyticsHelper.logWeatherFetched(
                    location = _mainUiState.value.settingsBundle.location?.name ?: "unknown"
                )
            } catch (e: Exception) {
                setNetworkError(e.toApiError())
                analyticsHelper.logWeatherFetchFailed(
                    location = _mainUiState.value.settingsBundle.location?.name ?: "unknown",
                    error = e.message ?: "unknown"
                )
            }
            _mainUiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun updateWeatherState(data: WeatherResponse) {
        val state = withContext(Dispatchers.Default) {
            buildWeatherDataState(_mainUiState.value, data)
        }
        _mainUiState.update { _ ->
            state
        }
    }

    private fun buildWeatherDataState(
        state: MainUIState,
        data: WeatherResponse,
    ): MainUIState {
        var state = state
        state = state.copy(
            weatherData = data
        )
        val weatherMetrics = weatherMetricsFactory.create(
            data,
            state.isOneDay,
            state.confirmedDates.start,
            state.confirmedDates.end
        )
        state = state.copy(weatherMetrics = weatherMetrics)
        return state
    }
}