package com.hsact.sunplanner.data.utils

import com.hsact.sunplanner.data.responses.DailyWeather
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object WeatherUtils {
    fun filterDailyWeatherByDateRange(
        dailyWeather: DailyWeather?,
        fromDate: LocalDate,
        toDate: LocalDate
    ): DailyWeather? {
        if (dailyWeather == null) { return null }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val startMonthDay = fromDate.monthValue * 100 + fromDate.dayOfMonth
        val endMonthDay = toDate.monthValue * 100 + toDate.dayOfMonth

        val matchingIndices = dailyWeather.time.mapIndexedNotNull { index, dateStr ->
            val date = LocalDate.parse(dateStr, formatter)
            val currentMonthDay = date.monthValue * 100 + date.dayOfMonth

            val isInRange = if (startMonthDay <= endMonthDay) {
                currentMonthDay in startMonthDay..endMonthDay
            } else {
                currentMonthDay >= startMonthDay || currentMonthDay <= endMonthDay
            }
            if (isInRange) index else null
        }

        return DailyWeather(
            time = matchingIndices.map { dailyWeather.time[it] },
            code = matchingIndices.map { dailyWeather.code[it] },
            maxTemperature = matchingIndices.map { dailyWeather.maxTemperature[it] },
            minTemperature = matchingIndices.map { dailyWeather.minTemperature[it] },
            apparentMaxTemperature = matchingIndices.map { dailyWeather.apparentMaxTemperature[it] },
            apparentMinTemperature = matchingIndices.map { dailyWeather.apparentMinTemperature[it] },
            precipitationSum = matchingIndices.map { dailyWeather.precipitationSum[it] },
            precipitationHours = matchingIndices.map { dailyWeather.precipitationHours[it] },
            sunshineDuration = matchingIndices.map { dailyWeather.sunshineDuration[it] },
            windSpeedMax = matchingIndices.map { dailyWeather.windSpeedMax[it] },
            windGustsMax = matchingIndices.map { dailyWeather.windGustsMax[it] },
        )
    }
}