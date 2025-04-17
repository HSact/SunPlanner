package com.hsact.sunplanner.domain.usecase

import com.hsact.sunplanner.data.responses.DailyWeather
import com.hsact.sunplanner.domain.model.DailyAggregatedData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class AggregateWeatherByDateUseCase {
    fun execute(daily: DailyWeather): List<DailyAggregatedData> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val grouped = daily.time.mapIndexed { index, dateStr ->
            val dayKey = LocalDate.parse(dateStr, formatter).format(DateTimeFormatter.ofPattern("MM-dd"))
            dayKey to index
        }.groupBy({ it.first }, { it.second })

        return grouped.map { (day, indices) ->
            val avg = { list: List<Double> -> indices.map { list[it] }.average() }
            val avgHours = { list: List<Double> -> indices.map { list[it] }.average() }

            DailyAggregatedData(
                date = day,
                avgMaxTemp = (avg(daily.maxTemperature) * 10).roundToInt() / 10.0,
                avgMinTemp = (avg(daily.minTemperature) * 10).roundToInt() / 10.0,
                avgSunshineSeconds = (avgHours(daily.sunshineDuration) * 10).roundToInt() / 10.0,
                avgPrecipitation = (avg(daily.precipitationSum) * 10).roundToInt() / 10.0,
                avgWindSpeed = (avg(daily.windSpeedMax) * 10).roundToInt() / 10.0,
            )
        }.sortedBy { it.date }
    }
}