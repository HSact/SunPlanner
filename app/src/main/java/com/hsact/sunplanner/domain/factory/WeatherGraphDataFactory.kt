package com.hsact.sunplanner.domain.factory

import com.hsact.sunplanner.data.utils.StringProvider
import com.hsact.sunplanner.domain.model.WeatherGraphData
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphBarsUseCase
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphLineUseCase
import com.hsact.sunplanner.ui.theme.avgTempLineColor
import com.hsact.sunplanner.ui.theme.daylightLineColor
import com.hsact.sunplanner.ui.theme.maxTempLineColor
import com.hsact.sunplanner.ui.theme.minTempLineColor
import com.hsact.sunplanner.ui.theme.precipitationBarColor
import com.hsact.sunplanner.ui.theme.sunShineLineColor
import com.hsact.sunplanner.ui.theme.windGustsSpeedColor
import com.hsact.sunplanner.ui.theme.windSpeedColor
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
                color = maxTempLineColor,
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
                color = avgTempLineColor,
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
                color = minTempLineColor,
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
                color = sunShineLineColor,
                tintOpacity = 0.8F,
                isOneYear = isOneYear
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
                isOneYear = isOneYear
            )

        graphData.precipitation =
            createWeatherGraphBarsUseCase.invoke("", weatherMetrics.precipitation, precipitationBarColor)

        graphData.windSpeed =
            createWeatherGraphLineUseCase.invoke(
                label = stringProvider.wind(),
                values = weatherMetrics.windSpeed,
                dates = popUpLabels,
                isDotsVisible = isDotsVisible,
                isEdgesCurved = isEdgesCurved,
                color = windSpeedColor,
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
                color = windGustsSpeedColor,
                tintOpacity = 0.5F,
                isOneYear = isOneYear
            )
        return graphData
    }
}