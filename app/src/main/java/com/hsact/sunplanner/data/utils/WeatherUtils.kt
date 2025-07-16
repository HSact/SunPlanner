package com.hsact.sunplanner.data.utils

import com.hsact.sunplanner.data.responses.DailyWeather
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Utility object for filtering daily weather data based on a date range.
 */
object WeatherUtils {
    /**
     * Filters the provided [DailyWeather] object to include only entries that match the date range
     * specified by [fromDate] and [toDate]. The comparison is done by day and month, ignoring the year.
     *
     * This allows filtering across multiple years using only the day and month components.
     * For example, January 1st of any year will match if [fromDate] is January 1st.
     *
     * Handles the case where the date range wraps around the end of the year, e.g., from December 20 to January 10.
     *
     * @param dailyWeather The original weather data to be filtered. May be `null`.
     * @param fromDate The start of the date range to filter by.
     * @param toDate The end of the date range to filter by.
     * @return A new [DailyWeather] object containing only the entries within the specified date range,
     *         or `null` if the input [dailyWeather] is `null`.
     */
    fun filterDailyWeatherByDateRange(
        dailyWeather: DailyWeather?,
        fromDate: LocalDate,
        toDate: LocalDate
    ): DailyWeather? {
        if (dailyWeather == null) {
            return null
        }
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
            daylightDuration = matchingIndices.map { dailyWeather.daylightDuration[it] },
            windSpeedMax = matchingIndices.map { dailyWeather.windSpeedMax[it] },
            windGustsMax = matchingIndices.map { dailyWeather.windGustsMax[it] },
        )
    }
}