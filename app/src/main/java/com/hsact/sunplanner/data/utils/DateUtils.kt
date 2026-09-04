package com.hsact.sunplanner.data.utils

import android.util.Log
import java.time.LocalDate
import java.time.MonthDay
import java.time.Year
import java.time.format.TextStyle
import java.util.Locale

/**
 * Utility object for date formatting and label generation used in weather data visualization.
 */
object DateUtils {
    /**
     * Formats a date range into a localized string.
     * 
     * Supports special formatting for:
     * - Single day vs Range of days.
     * - Single year vs Range of years.
     * - Russian genitive case for months (e.g., "11 сентября" instead of "11 Сентябрь").
     */
    fun formatDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        isOneDay: Boolean,
        isOneYear: Boolean,
        locale: Locale,
        singleDayOneYearString: String = "",
        singleDaySting: String = "",
        dateRangeOneYearSting: String = "",
        dateRangeString: String = ""
    ): String {
        val russianGenitiveMonths = listOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        )

        fun getMonthName(date: LocalDate): String {
            return if (locale.language == "ru") {
                russianGenitiveMonths[date.monthValue - 1]
            } else {
                date.month.getDisplayName(TextStyle.FULL, locale)
                    .replaceFirstChar { it.uppercase() }
            }
        }

        return if (isOneDay) {
            val monthName = getMonthName(startDate)
            if (isOneYear) {
                String.format(
                    singleDayOneYearString,
                    startDate.dayOfMonth,
                    monthName,
                    startDate.year
                )
            } else {
                String.format(
                    singleDaySting,
                    startDate.dayOfMonth,
                    monthName,
                    startDate.year,
                    endDate.year
                )
            }
        } else {
            val monthName1 = getMonthName(startDate)
            val monthName2 = getMonthName(endDate)
            if (isOneYear) {
                String.format(
                    dateRangeOneYearSting,
                    startDate.dayOfMonth,
                    monthName1,
                    endDate.dayOfMonth,
                    monthName2,
                    startDate.year,
                )
            } else {
                String.format(
                    dateRangeString,
                    startDate.dayOfMonth,
                    monthName1,
                    endDate.dayOfMonth,
                    monthName2,
                    startDate.year,
                    endDate.year
                )
            }
        }
    }

    /**
     * Generates X-axis labels for popup tooltips.
     * 
     * In RU locale, ensures months are correctly abbreviated (e.g., "сент." instead of "Сен").
     * Returns either a list of years (for single-day windows) or "Day Month" strings.
     */
    fun generatePopUpLabels(
        startDate: LocalDate,
        endDate: LocalDate,
        locale: Locale
    ): List<String> {
        val isOneDay = startDate.dayOfMonth == endDate.dayOfMonth &&
                startDate.month == endDate.month

        if (isOneDay) {
            return (startDate.year..endDate.year).map { it.toString() }
        }

        val sequence = generateSafeDaySequence(startDate, endDate)
        val russianMonthLabels = listOf(
            "янв.", "февр.", "марта", "апр.", "мая", "июня",
            "июля", "авг.", "сент.", "окт.", "нояб.", "дек."
        )

        return sequence.map { date ->
            val day = date.dayOfMonth
            val month = if (locale.language == "ru") {
                russianMonthLabels[date.monthValue - 1]
            } else {
                date.month.getDisplayName(TextStyle.SHORT, locale)
            }
            "$day $month"
        }
    }

    /**
     * Determines kind of X-axis labels to generate.
     */
    fun generateAxisXLabels(
        startDate: LocalDate,
        endDate: LocalDate,
        locale: Locale
    ): List<String> {
        val isOneDayWindow = startDate.dayOfMonth == endDate.dayOfMonth &&
                startDate.month == endDate.month

        if (isOneDayWindow) {
            return (startDate.year..endDate.year).map { "'${(it % 100).toString().padStart(2, '0')}" }
        }

        val sequence = generateSafeDaySequence(startDate, endDate)
        return if (sequence.size <= 92) {
            sequence.map { it.dayOfMonth.toString() }
        } else {
            generateMonthLabels(startDate, endDate, locale)
        }
    }

    /**
     * Generates a sequence of dates for the window, accounting for leap years in history depth.
     */
    private fun generateSafeDaySequence(start: LocalDate, end: LocalDate): List<LocalDate> {
        val anyLeap = (start.year..end.year).any { Year.isLeap(it.toLong()) }
        val baseYear = if (anyLeap) 2024 else 2023
        
        val startMD = MonthDay.of(start.month, start.dayOfMonth)
        val endMD = MonthDay.of(end.month, end.dayOfMonth)
        
        val current = try {
            startMD.atYear(baseYear)
        } catch (e: Exception) {
            Log.e("DateUtils", "Error adjusting date to year $baseYear", e)
            startMD.atYear(2024)
        }
        
        val target = if (!startMD.isAfter(endMD)) {
            endMD.atYear(current.year)
        } else {
            endMD.atYear(current.year + 1)
        }

        val result = mutableListOf<LocalDate>()
        var temp = current
        while (!temp.isAfter(target)) {
            if (temp.monthValue == 2 && temp.dayOfMonth == 29 && !anyLeap) {
                temp = temp.plusDays(1)
                continue
            }
            result.add(temp)
            temp = temp.plusDays(1)
        }
        return result
    }

    private fun generateMonthLabels(start: LocalDate, end: LocalDate, locale: Locale): List<String> {
        val russianMonthLabels = listOf(
            "янв.", "февр.", "март", "апр.", "май", "июнь",
            "июль", "авг.", "сент.", "окт.", "нояб.", "дек."
        )
        val startMD = MonthDay.of(start.month, start.dayOfMonth)
        val endMD = MonthDay.of(end.month, end.dayOfMonth)

        val current = startMD.atYear(2023).withDayOfMonth(1)
        val target = if (!startMD.isAfter(endMD)) {
            endMD.atYear(2023).withDayOfMonth(1)
        } else {
            endMD.atYear(2024).withDayOfMonth(1)
        }

        val labels = mutableListOf<String>()
        var temp = current
        while (!temp.isAfter(target)) {
            var label = temp.month.getDisplayName(TextStyle.SHORT, locale)
            if (locale.language == "ru") {
                label = russianMonthLabels[temp.monthValue - 1]
            }
            labels.add(label)
            temp = temp.plusMonths(1)
        }
        return labels
    }

    fun reduceAxisXLabels(
        rawLabels: List<String>,
        labelsWidth: Double,
        maxWidth: Double
    ): List<String> {
        if (labelsWidth <= maxWidth) {
            return rawLabels
        }
        val step = (labelsWidth / maxWidth).toInt().coerceAtLeast(1) + 1
        return rawLabels.mapIndexed { index, label ->
            if (index % step == 0) label else ""
        }
    }

    /**
     * Ensures the day of month is valid for the current month/year.
     * If the day exceeds the maximum days in the month (e.g., Feb 30), 
     * it is coerced to the last day of that month.
     */
    fun coerceDay(date: LocalDate): LocalDate {
        val maxDay = date.lengthOfMonth()
        return if (date.dayOfMonth > maxDay) date.withDayOfMonth(maxDay) else date
    }
}
