package com.hsact.sunplanner.data.utils

import java.time.LocalDate
import java.time.MonthDay
import java.time.Year
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Utility object for date formatting and label generation used in weather data visualization.
 */
object DateUtils {
    /**
     * Formats a date range into a localized string.
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
        return if (isOneDay) {
            val monthName = startDate.month
                .getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { it.uppercase() }
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
            val monthName1 = startDate.month
                .getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { it.uppercase() }
            val monthName2 = endDate.month
                .getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { it.uppercase() }
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
            "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
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
        val useYearsAsLabels = startDate.dayOfMonth == endDate.dayOfMonth &&
                startDate.month == endDate.month

        if (useYearsAsLabels) {
            return (startDate.year..endDate.year).map { "'${(it % 100).toString().padStart(2, '0')}" }
        }

        val daysCount = calculateDaysInWindow(startDate, endDate)
        return if (daysCount <= 92) {
            generateSafeDaySequence(startDate, endDate).map { it.dayOfMonth.toString() }
        } else {
            generateMonthLabels(startDate, endDate, locale)
        }
    }

    /**
     * Calculates the number of days in the seasonal window, regardless of specific years.
     */
    private fun calculateDaysInWindow(start: LocalDate, end: LocalDate): Int {
        val anyLeap = (start.year..end.year).any { Year.isLeap(it.toLong()) }
        val baseYear = if (anyLeap) 2024 else 2023
        val d1 = MonthDay.of(start.month, start.dayOfMonth).atYear(baseYear)
        var d2 = MonthDay.of(end.month, end.dayOfMonth).atYear(baseYear)
        if (d1.isAfter(d2)) {
            d2 = d2.plusYears(1)
        }
        return ChronoUnit.DAYS.between(d1, d2).toInt() + 1
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
            // Handle case where start is Feb 29 but baseYear is not leap
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
            // Only add Feb 29 if history depth actually contains a leap year
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
            "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
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
        return rawLabels.filterIndexed { index, _ -> index % step == 0 }
    }
}
