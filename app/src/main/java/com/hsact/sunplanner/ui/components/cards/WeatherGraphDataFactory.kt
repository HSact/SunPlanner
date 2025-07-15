package com.hsact.sunplanner.ui.components.cards

import com.hsact.sunplanner.domain.model.WeatherGraphData
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.repository.StringProvider
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphBarsUseCase
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphLineUseCase
import com.hsact.sunplanner.ui.theme.ExtendedColors
import javax.inject.Inject

class WeatherGraphDataFactory @Inject constructor(
    private val createWeatherGraphLineUseCase: CreateWeatherGraphLineUseCase,
    private val createWeatherGraphBarsUseCase: CreateWeatherGraphBarsUseCase,
    private val stringProvider: StringProvider
) {
    fun create(
        weatherMetrics: WeatherMetrics,
        isDotsVisible: Boolean,
        isEdgesCurved: Boolean,
        isOneYear: Boolean,
        colors: ExtendedColors,
        popUpLabels: List<String>
    ): WeatherGraphData {
        val graphData = WeatherGraphData()
        graphData.maxTemperature =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.max(),
                values = weatherMetrics.maxTemps,
                dates = popUpLabels,
                isDotsVisible = isDotsVisible,
                isEdgesCurved = isEdgesCurved,
                color = colors.maxTempLineColor,
                tintOpacity = 0.4F,
                isOneYear = isOneYear
            )
        graphData.avgTemperature =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.avg(),
                values = weatherMetrics.averageTemps,
                dates = popUpLabels,
                isDotsVisible = isDotsVisible,
                isEdgesCurved = isEdgesCurved,
                color = colors.avgTempLineColor,
                tintOpacity = 0.0F,
                isOneYear = isOneYear
            )

        graphData.minTemperature =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.min(),
                values = weatherMetrics.minTemps,
                dates = popUpLabels,
                isDotsVisible = isDotsVisible,
                isEdgesCurved = isEdgesCurved,
                color = colors.minTempLineColor,
                tintOpacity = 0.4F,
                isOneYear = isOneYear
            )

        graphData.sunShineDuration =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.sunshine(),
                values = weatherMetrics.sunshine,
                dates = popUpLabels,
                isDotsVisible = isDotsVisible,
                isEdgesCurved = isEdgesCurved,
                color = colors.sunShineLineColor,
                tintOpacity = 0.8F,
                isOneYear = isOneYear
            )

        graphData.dayLightDuration =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.daylight(),
                values = weatherMetrics.dayLight,
                dates = popUpLabels,
                isDotsVisible = false,
                isEdgesCurved = false,
                color = colors.daylightLineColor,
                tintOpacity = 0.0F,
                isOneYear = isOneYear
            )

        graphData.precipitation =
            createWeatherGraphBarsUseCase.invoke("", weatherMetrics.precipitation,
                colors.precipitationBarColor
            )

        graphData.windSpeed =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.wind(),
                values = weatherMetrics.windSpeed,
                dates = popUpLabels,
                isDotsVisible = isDotsVisible,
                isEdgesCurved = isEdgesCurved,
                color = colors.windSpeedColor,
                tintOpacity = 0.5F,
                isOneYear = isOneYear
            )

        graphData.windGustsSpeed =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.gusts(),
                values = weatherMetrics.gustSpeed,
                dates = popUpLabels,
                isDotsVisible = isDotsVisible,
                isEdgesCurved = isEdgesCurved,
                color = colors.windGustsSpeedColor,
                tintOpacity = 0.5F,
                isOneYear = isOneYear
            )
        return graphData
    }
}