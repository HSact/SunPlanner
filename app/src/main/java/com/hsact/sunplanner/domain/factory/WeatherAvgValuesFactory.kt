package com.hsact.sunplanner.domain.factory

import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.domain.usecase.weather.AggregateWeatherByDateUseCase
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * A factory class responsible for generating average weather metric values
 * from raw weather API data.
 *
 * It uses [AggregateWeatherByDateUseCase] to aggregate daily weather data by date
 * and then fills the [WeatherMetrics] object with averaged values.
 *
 * @property aggregateWeatherByDateUseCase Use case for aggregating weather data by date.
 */
class WeatherAvgValuesFactory @Inject constructor(
    private val aggregateWeatherByDateUseCase: AggregateWeatherByDateUseCase
) {

    /**
     * Creates and populates a [WeatherMetrics] instance with averaged weather data
     * based on the provided raw [WeatherResponse].
     *
     * The following fields are populated with averaged values:
     * - Max, average, and min temperatures
     * - Sunshine duration (in hours, rounded to 1 decimal)
     * - Daylight duration (in hours, rounded to 1 decimal)
     * - Precipitation sum
     * - Wind speed and gusts
     *
     * **Note:** This method mutates the input [weatherMetrics] object and also returns it.
     *
     * @param data Raw weather response containing daily weather information.
     * @param weatherMetrics The target [WeatherMetrics] object to populate.
     * @return The same [WeatherMetrics] instance, updated with average values.
     */
    fun create(
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