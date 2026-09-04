package com.hsact.sunplanner.ui.detailscreen

import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.model.DetailedYearlyData
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.WeatherMetricType
import java.time.LocalDate

data class WeatherDetailUiState(
    val metricType: WeatherMetricType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isLoading: Boolean = true,
    val title: String = "",
    val cityName: String = "",
    val compCityName: String = "",
    val isMainVisible: Boolean = true,
    val isCompVisible: Boolean = true,
    val displayMode: DetailDisplayMode = DetailDisplayMode.LIST,
    val yearlyData: List<DetailedYearlyData> = emptyList(),
    val compYearlyData: List<DetailedYearlyData> = emptyList(),
    val settings: SettingsBundle = SettingsBundle(),
    val summary: WeatherDetailSummary = WeatherDetailSummary(),
    val compSummary: WeatherDetailSummary = WeatherDetailSummary(),
    val insights: List<String> = emptyList(),
    val error: ApiError? = null
)