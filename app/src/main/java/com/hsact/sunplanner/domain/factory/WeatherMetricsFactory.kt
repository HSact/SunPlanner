package com.hsact.sunplanner.domain.factory

import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.usecase.weather.AggregateWeatherByDateUseCase
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Factory class responsible for converting raw [WeatherResponse] data into a domain-specific
 * [WeatherMetrics] model, optionally performing aggregation if the data spans multiple days.
 */
class WeatherMetricsFactory @Inject constructor(
    private val aggregateWeatherByDateUseCase: AggregateWeatherByDateUseCase
) {

    /**
     * Creates a [WeatherMetrics] object from the given [WeatherResponse].
     */
    fun create(
        data: WeatherResponse, 
        isOneDay: Boolean, 
        filterStart: LocalDate, 
        filterEnd: LocalDate
    ): WeatherMetrics {
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
            weatherMetrics = createAverage(data, weatherMetrics, filterStart, filterEnd)
        }
        return weatherMetrics
    }

    private fun createAverage(
        data: WeatherResponse,
        weatherMetrics: WeatherMetrics,
        filterStart: LocalDate,
        filterEnd: LocalDate
    ): WeatherMetrics {
        val aggregated = aggregateWeatherByDateUseCase.execute(data.daily, filterStart, filterEnd)
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
        return weatherMetrics
    }
}
