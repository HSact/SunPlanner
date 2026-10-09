package com.hsact.sunplanner.ui.mainscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.data.utils.LocationUtils
import com.hsact.sunplanner.domain.analytics.AnalyticsHelper
import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.error.toApiError
import com.hsact.sunplanner.domain.factory.WeatherMetricsFactory
import com.hsact.sunplanner.domain.model.Bookmark
import com.hsact.sunplanner.domain.model.DatesBundle
import com.hsact.sunplanner.domain.model.DatesBundleSerializable
import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.PrecipitationUnitMode
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode
import com.hsact.sunplanner.domain.monitoring.CrashReportingHelper
import com.hsact.sunplanner.domain.monitoring.PerformanceHelper
import com.hsact.sunplanner.domain.repository.BookmarkRepository
import com.hsact.sunplanner.domain.repository.HistoryRepository
import com.hsact.sunplanner.domain.repository.StringProvider
import com.hsact.sunplanner.di.DefaultDispatcher
import com.hsact.sunplanner.domain.repository.WeatherRepository
import com.hsact.sunplanner.domain.usecase.settings.CompleteOnboardingUseCase
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLocationUseCase
import com.hsact.sunplanner.domain.usecase.weather.FetchFilteredWeatherUseCase
import com.hsact.sunplanner.ui.utils.AppLocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

/**
 * ViewModel for the Main Screen responsible for managing weather searches, location selection,
 * comparison mode, and bookmarks.
 * 
 * It coordinates data fetching from multiple use cases and updates the [MainUIState] 
 * for the UI layer to consume.
 */
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
    private val bookmarkRepository: BookmarkRepository,
    private val historyRepository: HistoryRepository,
    private val appLocationManager: AppLocationManager,
    private val crashReportingHelper: CrashReportingHelper,
    private val performanceHelper: PerformanceHelper,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUIState())
    val mainUiState: StateFlow<MainUIState> = _mainUiState.asStateFlow()

    init {
        analyticsHelper.logAppStarted()

        viewModelScope.launch {
            combine(
                getSettingsUseCase.location,
                getSettingsUseCase.isDotsVisible,
                getSettingsUseCase.isEdgesCurved,
                getSettingsUseCase.language,
                getSettingsUseCase.theme,
                getSettingsUseCase.temperatureUnit,
                getSettingsUseCase.windUnit,
                getSettingsUseCase.precipitationUnit
            ) { args ->
                val location = args[0] as? Location
                val isDotsVisible = args[1] as Boolean
                val isEdgesCurved = args[2] as Boolean
                val language = args[3] as? LanguageMode
                val theme = args[4] as ThemeMode
                val tempUnit = args[5] as TemperatureUnitMode
                val windUnit = args[6] as WindSpeedUnitMode
                val precipitationUnit = args[7] as PrecipitationUnitMode

                SettingsBundle(
                    location = location,
                    isDotsVisible = isDotsVisible,
                    isEdgesCurved = isEdgesCurved,
                    languageMode = language ?: LanguageMode.fromName(Locale.getDefault().language),
                    themeMode = theme,
                    temperatureUnitMode = tempUnit,
                    windUnitMode = windUnit,
                    precipitationUnitMode = precipitationUnit
                )
            }.debounce(200).collect { updatedBundle ->
                val oldLocation = _mainUiState.value.settingsBundle.location
                val isLocationChanged = updatedBundle.location != oldLocation

                _mainUiState.update {
                    it.copy(
                        settingsBundle = updatedBundle,
                        cityName = if (isLocationChanged) {
                            updatedBundle.location?.let { loc -> LocationUtils.buildCityFullName(loc) } ?: ""
                        } else {
                            it.cityName
                        },
                        // Clear weather data if location changed
                        weatherData = if (isLocationChanged) null else it.weatherData,
                        weatherMetrics = if (isLocationChanged) WeatherMetrics() else it.weatherMetrics,
                        comparisonWeatherData = if (isLocationChanged) null else it.comparisonWeatherData,
                        comparisonWeatherMetrics = if (isLocationChanged) null else it.comparisonWeatherMetrics
                    )
                }
                updateBookmarkStatus()

                // Only auto-refresh weather if location didn't change (e.g., unit change)
                // AND we already have data on screen.
                if (!isLocationChanged && _mainUiState.value.weatherData != null && !_mainUiState.value.isLoading) {
                    onWeatherSearchClick()
                }
            }
        }

        viewModelScope.launch {
            bookmarkRepository.bookmarks.collect { list ->
                _mainUiState.update { it.copy(bookmarks = list) }
                updateBookmarkStatus()
            }
        }

        viewModelScope.launch {
            historyRepository.history.collect { list ->
                _mainUiState.update { it.copy(searchHistory = list) }
            }
        }

        viewModelScope.launch {
            getSettingsUseCase.hasSeenOnboarding.collect { hasSeen ->
                if (!hasSeen) {
                    _mainUiState.update { it.copy(showOnboarding = true) }
                }
            }
        }
    }

    fun handleIntent(intent: MainScreenIntents) {
        viewModelScope.launch {
            when (intent) {
                is MainScreenIntents.DismissOnboarding -> {
                    _mainUiState.update { it.copy(showOnboarding = false) }
                    completeOnboardingUseCase(hasSeen = true)
                }

                is MainScreenIntents.OpenOnboardingGuide -> {
                    _mainUiState.update { it.copy(showOnboarding = true) }
                }

                is MainScreenIntents.FetchCityList -> fetchCityList(intent.query)
                is MainScreenIntents.UpdateSearchQuery -> {
                    _mainUiState.update { it.copy(searchQuery = intent.query) }
                }

                is MainScreenIntents.UpdateLocation -> updateLocation(intent.city)
                is MainScreenIntents.UpdateStartYear -> updateStartYear(intent.year)
                is MainScreenIntents.UpdateStartMonth -> updateStartMonth(intent.month)
                is MainScreenIntents.UpdateStartDay -> updateStartDay(intent.day)
                is MainScreenIntents.UpdateEndYear -> updateEndYear(intent.year)
                is MainScreenIntents.UpdateEndMonth -> updateEndMonth(intent.month)
                is MainScreenIntents.UpdateEndDay -> updateEndDay(intent.day)
                is MainScreenIntents.CleanValidationError -> cleanValidationError()
                is MainScreenIntents.CleanNetworkError -> cleanNetworkError()
                is MainScreenIntents.WeatherSearchClick -> onWeatherSearchClick()
                is MainScreenIntents.ToggleBookmark -> toggleBookmark()
                is MainScreenIntents.SelectBookmark -> selectBookmark(intent.bookmark)
                is MainScreenIntents.DeleteBookmark -> bookmarkRepository.removeBookmark(intent.id)
                is MainScreenIntents.UseCurrentLocation -> useCurrentLocation()
                is MainScreenIntents.ClearAppCache -> clearAppCache()

                is MainScreenIntents.ToggleComparisonMode -> {
                    _mainUiState.update { it.copy(isComparisonMode = !it.isComparisonMode) }
                }

                is MainScreenIntents.UpdateComparisonLocation -> {
                    _mainUiState.update {
                        it.copy(
                            comparisonLocation = intent.city,
                            weatherData = null,
                            weatherMetrics = WeatherMetrics(),
                            comparisonWeatherData = null,
                            comparisonWeatherMetrics = null
                        )
                    }
                }

                is MainScreenIntents.RemoveComparison -> {
                    _mainUiState.update {
                        it.copy(
                            comparisonLocation = null,
                            comparisonWeatherData = null,
                            comparisonWeatherMetrics = null,
                            isComparisonMode = false
                        )
                    }
                }

                is MainScreenIntents.DeleteHistoryItem -> {
                    historyRepository.removeFromHistory(intent.city)
                }
            }
        }
    }

    private fun useCurrentLocation() {
        viewModelScope.launch {
            Log.d("MainViewModel", "useCurrentLocation: Started")
            _mainUiState.update { it.copy(isLoading = true) }
            try {
                val location = appLocationManager.getCurrentLocation()
                if (location != null) {
                    Log.d("MainViewModel", "useCurrentLocation: Success, updating location to ${location.name}")
                    updateLocation(location)
                } else {
                    Log.w("MainViewModel", "useCurrentLocation: Failed to get location")
                    setValidationError(stringProvider.locationNotFound())
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "useCurrentLocation: Unexpected error", e)
                crashReportingHelper.recordException(e)
                setValidationError(stringProvider.locationNotFound())
            } finally {
                Log.d("MainViewModel", "useCurrentLocation: Setting isLoading = false")
                _mainUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun clearAppCache() {
        viewModelScope.launch {
            repository.clearCache()
            bookmarkRepository.clearAll()
            _mainUiState.update {
                it.copy(
                    weatherData = null,
                    weatherMetrics = WeatherMetrics(),
                    comparisonLocation = null,
                    comparisonWeatherData = null,
                    comparisonWeatherMetrics = null,
                    isComparisonMode = false
                )
            }
        }
    }

    /**
     * Updates the active location in persistent settings.
     * Also updates the [MainUIState.cityName] to reflect the new location.
     *
     * @param city The [Location] to set as active.
     */
    private suspend fun updateLocation(city: Location) {
        Log.d("MainViewModel", "updateLocation: Setting city to ${city.name}")
        _mainUiState.update {
            it.copy(
                cityName = LocationUtils.buildCityFullName(city),
                weatherData = null,
                weatherMetrics = WeatherMetrics(),
                comparisonWeatherData = null,
                comparisonWeatherMetrics = null
            )
        }
        updateLocationUseCase.invoke(city)
        updateBookmarkStatus()
        historyRepository.addToHistory(city)
    }

    private fun toggleBookmark() {
        viewModelScope.launch {
            val state = _mainUiState.value
            val location = state.settingsBundle.location ?: return@launch
            val dates = state.confirmedDates

            val bookmarkId = generateBookmarkId(location, dates)

            if (state.isBookmarked) {
                bookmarkRepository.removeBookmark(bookmarkId)
            } else {
                val bookmark = Bookmark(
                    id = bookmarkId,
                    location = location,
                    dates = DatesBundleSerializable(
                        dates.start.year, dates.start.monthValue, dates.start.dayOfMonth,
                        dates.end.year, dates.end.monthValue, dates.end.dayOfMonth
                    )
                )
                bookmarkRepository.addBookmark(bookmark)
            }
        }
    }

    private suspend fun selectBookmark(bookmark: Bookmark) {
        val newDates = DatesBundle(
            LocalDate.of(
                bookmark.dates.startYear,
                bookmark.dates.startMonth,
                bookmark.dates.startDay
            ),
            LocalDate.of(
                bookmark.dates.endYear,
                bookmark.dates.endMonth,
                bookmark.dates.endDay
            )
        )

        // Update state immediately to avoid race conditions with Search click
        _mainUiState.update {
            it.copy(
                settingsBundle = it.settingsBundle.copy(location = bookmark.location),
                tempDates = newDates,
                confirmedDates = newDates,
                cityName = LocationUtils.buildCityFullName(bookmark.location)
            )
        }

        // Sync with persistent storage
        updateLocation(bookmark.location)

        // Auto-trigger weather search
        onWeatherSearchClick()
    }

    private fun updateBookmarkStatus() {
        val state = _mainUiState.value
        val location = state.settingsBundle.location ?: return
        val currentId = generateBookmarkId(location, state.confirmedDates)
        val isBookmarked = state.bookmarks.any { it.id == currentId }
        _mainUiState.update { it.copy(isBookmarked = isBookmarked) }
    }

    private fun generateBookmarkId(location: Location, dates: DatesBundle): String {
        return "${location.name}_${location.latitude}_${location.longitude}_${dates.start}_${dates.end}"
    }

    private fun updateStartYear(year: Int) = updateStartDate { it.withYear(year) }
    private fun updateStartMonth(month: Int) = updateStartDate { it.withMonth(month) }
    private fun updateStartDay(day: Int) =
        updateStartDate { it.withDayOfMonth(day.coerceIn(1, it.lengthOfMonth())) }

    private fun updateEndYear(year: Int) = updateEndDate { it.withYear(year) }
    private fun updateEndMonth(month: Int) = updateEndDate { it.withMonth(month) }
    private fun updateEndDay(day: Int) =
        updateEndDate { it.withDayOfMonth(day.coerceIn(1, it.lengthOfMonth())) }

    private fun updateStartDate(transform: (LocalDate) -> LocalDate) {
        _mainUiState.update { state ->
            val newDate = DateUtils.coerceDay(transform(state.tempDates.start))
            state.copy(tempDates = state.tempDates.copy(start = newDate))
        }
    }

    private fun updateEndDate(transform: (LocalDate) -> LocalDate) {
        _mainUiState.update { state ->
            val newDate = DateUtils.coerceDay(transform(state.tempDates.end))
            state.copy(tempDates = state.tempDates.copy(end = newDate))
        }
    }

    private fun setValidationError(error: String) =
        _mainUiState.update { it.copy(validationError = error) }

    private fun setNetworkError(error: ApiError) = _mainUiState.update {
        it.copy(
            networkError = error,
            networkErrorId = UUID.randomUUID().toString()
        )
    }

    private fun cleanValidationError() = _mainUiState.update { it.copy(validationError = null) }
    private fun cleanNetworkError() = _mainUiState.update { it.copy(networkError = null) }

    private suspend fun onWeatherSearchClick() {
        val state = _mainUiState.value
        Log.d("SunPlannerDebug", "onWeatherSearchClick triggered")

        if (!state.isLocationNotNull) {
            setValidationError(stringProvider.locationEmpty())
            return
        }
        if (!state.tempDates.isYearsRangeWithinLimit(state.maxYearRange)) {
            setValidationError(stringProvider.yearsRangeTooBig(state.maxYearRange))
            return
        }

        val targetDates = state.tempDates

        val params =
            prepareParamsForRequest(state.settingsBundle, targetDates) ?: return
        Log.d("SunPlannerDebug", "Params prepared: ${params.latitude}, ${params.longitude}")

        val traceName = "weather_fetch_trace"
        performanceHelper.startTrace(traceName)
        performanceHelper.putAttribute(traceName, "location", state.cityName)
        performanceHelper.putAttribute(traceName, "is_comparison", state.isComparisonMode.toString())

        _mainUiState.update { it.copy(isLoading = true) }

        try {
            coroutineScope {
                Log.d("SunPlannerDebug", "Executing weather fetch use case...")
                val mainDeferred = async {
                    fetchFilteredWeatherUseCase.execute(
                        params,
                        targetDates.start,
                        targetDates.end
                    )
                }

                val compDeferred =
                    if (state.isComparisonMode && state.comparisonLocation != null) {
                        Log.d(
                            "SunPlannerDebug",
                            "Comparison mode active. Fetching second location..."
                        )
                        val compParams = params.copy(
                            latitude = state.comparisonLocation.latitude,
                            longitude = state.comparisonLocation.longitude
                        )
                        async {
                            fetchFilteredWeatherUseCase.execute(
                                compParams,
                                targetDates.start,
                                targetDates.end
                            )
                        }
                    } else null

                val mainResponse = mainDeferred.await()
                Log.d("SunPlannerDebug", "Main weather response received")
                val compResponse = compDeferred?.await()
                if (compResponse != null) Log.d(
                    "SunPlannerDebug",
                    "Comparison weather response received"
                )

                updateWeatherState(mainResponse, compResponse, targetDates)
                performanceHelper.stopTrace(traceName)
            }
        } catch (e: Exception) {
            Log.e("SunPlannerDebug", "Critical error in fetchWeather", e)
            crashReportingHelper.recordException(e)
            performanceHelper.stopTrace(traceName)
            setNetworkError(e.toApiError())
        } finally {
            Log.d("SunPlannerDebug", "fetchWeather completed, setting isLoading = false")
            _mainUiState.update { it.copy(isLoading = false) }
        }
    }

    private fun prepareParamsForRequest(
        settings: SettingsBundle,
        dates: DatesBundle
    ): WeatherRequestParams? {
        val location = settings.location ?: return null
        return WeatherRequestParams().apply {
            latitude = location.latitude
            longitude = location.longitude
            this.startDate = dates.start.toString()
            this.endDate = dates.end.toString()
            this.temperatureUnit = settings.temperatureUnitMode.toName()
            this.windSpeedUnit = settings.windUnitMode.toName()
            this.precipitationUnit = settings.precipitationUnitMode.toName()
            this.daily = WeatherRequestParams.DEFAULT_DAILY_VARIABLES
        }
    }

    private fun fetchCityList(cityName: String) {
        viewModelScope.launch {
            _mainUiState.update { it.copy(isSearchingCities = true, searchQuery = cityName) }
            try {
                val cities = repository.getCitiesList(
                    cityName,
                    _mainUiState.value.settingsBundle.languageMode.toName()
                )
                _mainUiState.update {
                    it.copy(
                        cities = cities ?: emptyList(),
                        isSearchingCities = false
                    )
                }
            } catch (e: Exception) {
                _mainUiState.update { it.copy(isSearchingCities = false) }
                setNetworkError(e.toApiError())
            }
        }
    }

    private suspend fun updateWeatherState(
        mainData: WeatherResponse, 
        compData: WeatherResponse?,
        targetDates: DatesBundle
    ) {
        Log.d("SunPlannerDebug", "updateWeatherState started")
        val isOneDay = targetDates.start.month == targetDates.end.month &&
                targetDates.start.dayOfMonth == targetDates.end.dayOfMonth
        val start = targetDates.start
        val end = targetDates.end

        try {
            val (mainMetrics, compMetrics) = withContext(defaultDispatcher) {
                val m = weatherMetricsFactory.create(mainData, isOneDay, start, end)
                val c = compData?.let { weatherMetricsFactory.create(it, isOneDay, start, end) }
                m to c
            }
            Log.d(
                "SunPlannerDebug",
                "Metrics created successfully. Main size: ${mainMetrics.maxTemps.size}"
            )

            _mainUiState.update {
                it.copy(
                    confirmedDates = targetDates,
                    weatherData = mainData,
                    weatherMetrics = mainMetrics,
                    comparisonWeatherData = compData,
                    comparisonWeatherMetrics = compMetrics
                )
            }
            updateBookmarkStatus()
            Log.d("SunPlannerDebug", "MainUIState updated with data")
        } catch (e: Exception) {
            Log.e("SunPlannerDebug", "Error creating metrics", e)
            throw e
        }
    }
}
