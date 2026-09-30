package com.hsact.sunplanner.domain.usecase.weather

import com.hsact.sunplanner.data.responses.DailyWeather
import com.hsact.sunplanner.domain.model.DailyAggregatedData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Use case responsible for aggregating historical weather data across multiple years.
 * 
 * It groups data by day and month (MM-dd), calculating averages for temperatures, 
 * sunshine, precipitation, wind, and air quality. This provides a "typical" weather 
 * profile for the selected date range.
 */
class AggregateWeatherByDateUseCase @Inject constructor() {
    /**
     * Executes the aggregation logic.
     * 
     * @param daily The raw daily weather data spanning multiple years.
     * @param filterStart The start date of the target window (month/day).
     * @param filterEnd The end date of the target window (month/day).
     * @return A list of [DailyAggregatedData] sorted chronologically within the window.
     */
    fun execute(daily: DailyWeather, filterStart: LocalDate, filterEnd: LocalDate): List<DailyAggregatedData> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startMD = filterStart.monthValue * 100 + filterStart.dayOfMonth
        val endMD = filterEnd.monthValue * 100 + filterEnd.dayOfMonth
        val isWrapping = startMD > endMD

        val filteredIndices = daily.time.mapIndexedNotNull { index, dateStr ->
            val date = LocalDate.parse(dateStr, formatter)
            val currentMD = date.monthValue * 100 + date.dayOfMonth
            
            val isInRange = if (isWrapping) {
                currentMD !in (endMD + 1)..<startMD
            } else {
                currentMD in startMD..endMD
            }
            
            if (isInRange) dayToKey(date) to index else null
        }

        val grouped = filteredIndices.groupBy({ it.first }, { it.second })

        return grouped.map { (day, indices) ->
            val avg = { list: List<Double> -> indices.map { list[it] }.average() }
            val avgNullable =
                { list: List<Double?> -> indices.mapNotNull { list.getOrNull(it) }.average() }
            val mode = { list: List<Double> ->
                indices.map { list[it].toInt() }
                    .groupBy { it }
                    .maxByOrNull { it.value.size }?.key ?: 0
            }

            DailyAggregatedData(
                date = day,
                avgMaxTemp = (avg(daily.maxTemperature) * 10).roundToInt() / 10.0,
                avgAvgTemp = (((avg(daily.maxTemperature) + avg(daily.minTemperature)) / 2) * 10).roundToInt() / 10.0,
                avgMinTemp = (avg(daily.minTemperature) * 10).roundToInt() / 10.0,
                avgSunshineSeconds = (avg(daily.sunshineDuration) * 10).roundToInt() / 10.0,
                avgDaylightSeconds = (avg(daily.daylightDuration) * 10).roundToInt() / 10.0,
                avgPrecipitation = (avg(daily.precipitationSum) * 10).roundToInt() / 10.0,
                avgWindSpeed = (avg(daily.windSpeedMax) * 10).roundToInt() / 10.0,
                avgWindGustSpeed = (avg(daily.windGustsMax) * 10).roundToInt() / 10.0,
                avgAqi = (avgNullable(daily.european_aqi).takeIf { !it.isNaN() } ?: 0.0),
                commonWeatherCode = mode(daily.code)
            )
        }.sortedBy { aggregated ->
            val dateMD = monthDayToValue(aggregated.date)
            if (isWrapping) {
                if (dateMD >= startMD) dateMD - 1300 else dateMD
            } else {
                dateMD
            }
        }
    }

    private fun dayToKey(date: LocalDate): String = 
        date.format(DateTimeFormatter.ofPattern("MM-dd"))

    private fun monthDayToValue(mmDd: String): Int {
        val parts = mmDd.split("-")
        return parts[0].toInt() * 100 + parts[1].toInt()
    }
}
