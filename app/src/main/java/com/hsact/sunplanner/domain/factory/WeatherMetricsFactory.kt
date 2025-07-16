package com.hsact.sunplanner.domain.factory

import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.usecase.weather.AggregateWeatherByDateUseCase
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Factory class responsible for converting raw [WeatherResponse] data into a domain-specific
 * [WeatherMetrics] model, optionally performing aggregation if the data spans multiple days.
 *
 * This class is used to decouple raw API data transformation logic from the rest of the app's domain layer.
 *
 * @property aggregateWeatherByDateUseCase Use case for aggregating weather data by date.
 */
class WeatherMetricsFactory @Inject constructor(
    private val aggregateWeatherByDateUseCase: AggregateWeatherByDateUseCase
) {

    /**
     * Creates a [WeatherMetrics] object from the given [WeatherResponse].
     *
     * If the data represents a single day (`isOneDay == true`), it performs direct mapping
     * and averages some values like daylight duration.
     * Otherwise, it computes average values using [aggregateWeatherByDateUseCase].
     *
     * All temperature values are rounded to 1 decimal place.
     * Sunshine and daylight durations are converted from seconds to hours and also rounded.
     *
     * @param data The raw weather response from the API.
     * @param isOneDay Flag indicating whether the data covers a single day or a longer range.
     * @return A populated [WeatherMetrics] instance.
     */
    fun create(data: WeatherResponse, isOneDay: Boolean): WeatherMetrics {
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
            weatherMetrics = createAverage(data, weatherMetrics)
        }
        return weatherMetrics
    }

    /**
     * Applies averaging logic to the provided [WeatherMetrics] based on the aggregated daily data.
     *
     * Uses [aggregateWeatherByDateUseCase] to compute average values across the given date range.
     * Converts durations from seconds to hours and rounds values to 1 decimal place where applicable.
     *
     * @param data Raw weather response with detailed daily values.
     * @param weatherMetrics A partially filled [WeatherMetrics] object to be updated.
     * @return The same [WeatherMetrics] instance, populated with averaged values.
     */
    private fun createAverage(
        data: WeatherResponse,
        weatherMetrics: WeatherMetrics
    ): WeatherMetrics {
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
        return weatherMetrics
    }
}