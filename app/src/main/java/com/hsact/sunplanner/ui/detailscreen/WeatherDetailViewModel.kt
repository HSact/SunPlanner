package com.hsact.sunplanner.ui.detailscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.error.toApiError
import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.PrecipitationUnitMode
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WeatherMetricType
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.weather.FetchFilteredWeatherUseCase
import com.hsact.sunplanner.domain.usecase.weather.GetDetailedYearlyDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Detail Screen, handling the retrieval and presentation of highly granular
 * historical weather data (multi-year comparison for specific dates).
 * 
 * Manages:
 * - Loading detailed yearly blocks for primary and optional comparison locations.
 * - Toggling visibility of locations in the graph/table.
 * - Calculating statistical summaries and insights.
 */
@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val fetchFilteredWeatherUseCase: FetchFilteredWeatherUseCase,
    private val getDetailedYearlyDataUseCase: GetDetailedYearlyDataUseCase,
    private val analyticHelper: WeatherDetailAnalyticHelper
) : ViewModel() {

    private val metricType: WeatherMetricType =
        WeatherMetricType.valueOf(savedStateHandle.get<String>("metricType") ?: "TEMPERATURE")
    private val startDate: LocalDate = LocalDate.parse(savedStateHandle.get<String>("start"))
    private val endDate: LocalDate = LocalDate.parse(savedStateHandle.get<String>("end"))

    private val compLat: String? = savedStateHandle.get<String>("compLat")
    private val compLon: String? = savedStateHandle.get<String>("compLon")
    private val compName: String? = savedStateHandle.get<String>("compName")

    private val _uiState = MutableStateFlow(
        WeatherDetailUiState(
            metricType = metricType,
            startDate = startDate,
            endDate = endDate,
            displayMode = calculateDisplayMode(startDate, endDate)
        )
    )
    val uiState: StateFlow<WeatherDetailUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun calculateDisplayMode(start: LocalDate, end: LocalDate): DetailDisplayMode {
        val isOneDay = start.month == end.month && start.dayOfMonth == end.dayOfMonth
        val isOneYear = start.year == end.year
        return if (isOneDay || isOneYear) DetailDisplayMode.TABLE else DetailDisplayMode.LIST
    }

    fun toggleMainVisibility() {
        _uiState.update { it.copy(isMainVisible = !it.isMainVisible) }
    }

    fun toggleCompVisibility() {
        _uiState.update { it.copy(isCompVisible = !it.isCompVisible) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getSettingsUseCase.location,
                getSettingsUseCase.temperatureUnit,
                getSettingsUseCase.windUnit,
                getSettingsUseCase.precipitationUnit,
                getSettingsUseCase.isDotsVisible,
                getSettingsUseCase.isEdgesCurved,
                getSettingsUseCase.language,
                getSettingsUseCase.theme
            ) { args ->
                val location = args[0] as? Location
                val settings = SettingsBundle(
                    location = location,
                    temperatureUnitMode = args[1] as TemperatureUnitMode,
                    windUnitMode = args[2] as WindSpeedUnitMode,
                    precipitationUnitMode = args[3] as PrecipitationUnitMode,
                    isDotsVisible = args[4] as Boolean,
                    isEdgesCurved = args[5] as Boolean,
                    languageMode = (args[6] as? LanguageMode)
                        ?: LanguageMode.fromName(Locale.getDefault().language),
                    themeMode = args[7] as ThemeMode
                )
                if (location != null) fetchData(location, settings)
            }.collect()
        }
    }

    private suspend fun fetchData(location: Location, settings: SettingsBundle) {
        val params = WeatherRequestParams().apply {
            latitude = location.latitude
            longitude = location.longitude
            this.startDate = this@WeatherDetailViewModel.startDate.toString()
            this.endDate = this@WeatherDetailViewModel.endDate.toString()
            this.temperatureUnit = settings.temperatureUnitMode.toName()
            this.windSpeedUnit = settings.windUnitMode.toName()
            this.precipitationUnit = settings.precipitationUnitMode.toName()
            this.daily = WeatherRequestParams.DEFAULT_DAILY_VARIABLES
        }

        val locale = settings.languageMode.toLocale()

        try {
            coroutineScope {
                val mainDeferred =
                    async { fetchFilteredWeatherUseCase.execute(params, startDate, endDate) }
                val compDeferred = if (compLat != null && compLon != null) {
                    val cParams =
                        params.copy(latitude = compLat.toDouble(), longitude = compLon.toDouble())
                    async { fetchFilteredWeatherUseCase.execute(cParams, startDate, endDate) }
                } else null

                val mainResp = mainDeferred.await()
                val compResp = compDeferred?.await()

                val mainYearly = getDetailedYearlyDataUseCase.execute(mainResp)
                val compYearly = compResp?.let { getDetailedYearlyDataUseCase.execute(it) }

                val summary = analyticHelper.calculateSummary(mainYearly, metricType)
                val compSummary =
                    compYearly?.let { analyticHelper.calculateSummary(it, metricType) }
                        ?: WeatherDetailSummary()
                val insights = if (compYearly != null) {
                    analyticHelper.generateComparisonInsights(
                        mainYearly,
                        compYearly,
                        metricType,
                        location.name,
                        compName ?: ""
                    )
                } else {
                    analyticHelper.generateInsights(mainYearly, metricType, settings, locale)
                }

                _uiState.update {
                    it.copy(
                        cityName = location.name,
                        compCityName = compName ?: "",
                        yearlyData = mainYearly,
                        compYearlyData = compYearly ?: emptyList(),
                        summary = summary,
                        compSummary = compSummary,
                        insights = insights,
                        settings = settings,
                        isLoading = false,
                        error = null
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherDetailVM", "Error fetching detail weather data", e)
            _uiState.update { it.copy(isLoading = false, error = e.toApiError()) }
        }
    }
}
