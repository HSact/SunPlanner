package com.hsact.sunplanner.ui.components.cards

import androidx.compose.ui.graphics.Color
import com.hsact.sunplanner.domain.model.WeatherGraphData
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphBarsUseCase
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphLineUseCase
import com.hsact.sunplanner.ui.theme.ExtendedColors
import javax.inject.Inject

/**
 * Factory class responsible for creating [WeatherGraphData] from [WeatherMetrics].
 */
class WeatherGraphDataFactory @Inject constructor(
    private val createWeatherGraphLineUseCase: CreateWeatherGraphLineUseCase,
    private val createWeatherGraphBarsUseCase: CreateWeatherGraphBarsUseCase
) {
    /**
     * Creates a [WeatherGraphData] object which holds the ready-to-display graph series.
     * 
     * This method handles both single-location and dual-location (comparison) modes:
     * - In single mode, it generates a full set of lines (Min, Avg, Max for temperature).
     * - In comparison mode, it generates only average lines for both cities to maintain readability.
     *
     * @param weatherMetrics Primary location metrics. If null, and in comparison mode, secondary metrics take priority.
     * @param isDotsVisible Whether to show data points on line charts.
     * @param isEdgesCurved Whether to use smooth curves instead of sharp lines.
     * @param isOneYear True if the data represents a single continuous year (no multi-year averaging).
     * @param colors Extended color palette for different weather variables.
     * @param popUpLabels Localized labels for graph tooltips (dates/years).
     * @param labels Human-readable strings for line legends (Max, Min, etc.).
     * @param compMetrics Optional metrics for a second location to compare with.
     * @param mainCityName Name of the primary location.
     * @param compCityName Name of the comparison location.
     * @return A container with all processed graph data.
     */
    fun create(
        weatherMetrics: WeatherMetrics?,
        isDotsVisible: Boolean,
        isEdgesCurved: Boolean,
        isOneYear: Boolean,
        colors: ExtendedColors,
        popUpLabels: List<String>,
        labels: WeatherGraphLabels,
        compMetrics: WeatherMetrics? = null,
        mainCityName: String? = null,
        compCityName: String? = null
    ): WeatherGraphData {
        val graphData = WeatherGraphData()
        val isComparison = weatherMetrics != null && compMetrics != null

        // --- Temperature ---
        if (isComparison) {
            graphData.maxTemperature = createWeatherGraphLineUseCase.invoke(
                label = mainCityName ?: "City A",
                values = weatherMetrics.averageTemps, dates = popUpLabels,
                isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                color = colors.avgTempLineColor, isOneYear = isOneYear
            )
            graphData.avgTemperature = createWeatherGraphLineUseCase.invoke(
                label = compCityName ?: "City B",
                values = compMetrics.averageTemps, dates = popUpLabels,
                isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                color = colors.minTempLineColor, isOneYear = isOneYear
            )
        } else {
            val metrics = weatherMetrics ?: compMetrics
            if (metrics != null) {
                graphData.maxTemperature = createWeatherGraphLineUseCase.invoke(
                    label = labels.max, values = metrics.maxTemps, dates = popUpLabels,
                    isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                    color = colors.maxTempLineColor, isOneYear = isOneYear
                )
                graphData.avgTemperature = createWeatherGraphLineUseCase.invoke(
                    label = labels.avg, values = metrics.averageTemps, dates = popUpLabels,
                    isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                    color = colors.avgTempLineColor, isOneYear = isOneYear
                )
                graphData.minTemperature = createWeatherGraphLineUseCase.invoke(
                    label = labels.min, values = metrics.minTemps, dates = popUpLabels,
                    isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                    color = colors.minTempLineColor, isOneYear = isOneYear
                )
            }
        }

        // --- Air Quality ---
        if (isComparison) {
            graphData.airQuality = createWeatherGraphLineUseCase.invoke(
                label = mainCityName ?: "City A",
                values = weatherMetrics.airQuality, dates = popUpLabels,
                isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                color = Color(0xFF8BC34A), isOneYear = isOneYear
            )
            graphData.airQualityComp = createWeatherGraphLineUseCase.invoke(
                label = compCityName ?: "City B",
                values = compMetrics.airQuality, dates = popUpLabels,
                isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                color = Color(0xFF009688), isOneYear = isOneYear
            )
        } else {
            val metrics = weatherMetrics ?: compMetrics
            if (metrics != null) {
                graphData.airQuality = createWeatherGraphLineUseCase.invoke(
                    label = "AQI", values = metrics.airQuality, dates = popUpLabels,
                    isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                    color = Color(0xFF8BC34A), isOneYear = isOneYear
                )
            }
        }

        // --- Wind ---
        if (isComparison) {
            graphData.windSpeed = createWeatherGraphLineUseCase.invoke(
                label = mainCityName ?: "City A",
                values = weatherMetrics.windSpeed, dates = popUpLabels,
                isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                color = colors.windSpeedColor, isOneYear = isOneYear
            )
            graphData.windGustsSpeed = createWeatherGraphLineUseCase.invoke(
                label = compCityName ?: "City B",
                values = compMetrics.windSpeed, dates = popUpLabels,
                isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                color = colors.windGustsSpeedColor, isOneYear = isOneYear
            )
        } else {
            val metrics = weatherMetrics ?: compMetrics
            if (metrics != null) {
                graphData.windSpeed = createWeatherGraphLineUseCase.invoke(
                    label = labels.wind, values = metrics.windSpeed, dates = popUpLabels,
                    isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                    color = colors.windSpeedColor, isOneYear = isOneYear
                )
                graphData.windGustsSpeed = createWeatherGraphLineUseCase.invoke(
                    label = labels.gusts, values = metrics.gustSpeed, dates = popUpLabels,
                    isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                    color = colors.windGustsSpeedColor, isOneYear = isOneYear
                )
            }
        }

        // --- Sunshine ---
        if (isComparison) {
            graphData.sunShineDuration = createWeatherGraphLineUseCase.invoke(
                label = mainCityName!!,
                values = weatherMetrics.sunshine, dates = popUpLabels,
                isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                color = colors.sunShineLineColor, isOneYear = isOneYear
            )
            graphData.dayLightDuration = createWeatherGraphLineUseCase.invoke(
                label = compCityName!!,
                values = compMetrics.sunshine, dates = popUpLabels,
                isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                color = colors.daylightLineColor, isOneYear = isOneYear
            )
        } else {
            val metrics = weatherMetrics ?: compMetrics
            if (metrics != null) {
                graphData.sunShineDuration = createWeatherGraphLineUseCase.invoke(
                    label = labels.sunshine, values = metrics.sunshine, dates = popUpLabels,
                    isDotsVisible = isDotsVisible, isEdgesCurved = isEdgesCurved,
                    color = colors.sunShineLineColor, isOneYear = isOneYear
                )
                graphData.dayLightDuration = createWeatherGraphLineUseCase.invoke(
                    label = labels.daylight, values = metrics.dayLight, dates = popUpLabels,
                    isDotsVisible = false, isEdgesCurved = false,
                    color = colors.daylightLineColor, isOneYear = isOneYear
                )
            }
        }

        // --- Precipitation ---
        if (isComparison) {
            val mainBars = createWeatherGraphBarsUseCase.invoke(
                label = mainCityName ?: "",
                values = weatherMetrics.precipitation, color = colors.precipitationBarColor
            )
            val compBars = createWeatherGraphBarsUseCase.invoke(
                label = compCityName ?: "", values = compMetrics.precipitation,
                color = Color(0xFF00BCD4)
            )
            graphData.precipitation = listOf(mainBars, compBars)
        } else {
            val metrics = weatherMetrics ?: compMetrics
            if (metrics != null) {
                val bars = createWeatherGraphBarsUseCase.invoke(
                    label = "", values = metrics.precipitation, color = colors.precipitationBarColor
                )
                graphData.precipitation = listOf(bars)
            }
        }

        return graphData
    }
}
