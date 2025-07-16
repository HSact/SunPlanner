package com.hsact.sunplanner.data.utils

import java.time.LocalDate
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
     * The formatting depends on whether the range is a single day or spans one or multiple years.
     * Pre-formatted string templates are passed as arguments to allow localization via resources.
     *
     * @param startDate Start of the range.
     * @param endDate End of the range.
     * @param isOneDay Whether the range consists of a single day.
     * @param isOneYear Whether the range spans only one year.
     * @param locale Target locale for month names.
     * @param singleDayOneYearString Template for single day in one year.
     * @param singleDaySting Template for single day in multiple years.
     * @param dateRangeOneYearSting Template for date range in one year.
     * @param dateRangeString Template for date range in multiple years.
     * @return A localized string representing the date range.
     */
    fun formatDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        isOneDay: Boolean,
        isOneYear: Boolean,
        locale: Locale,
        singleDayOneYearString: String = "",    //For resource String
        singleDaySting: String = "",            //For resource String
        dateRangeOneYearSting: String = "",     //For resource String
        dateRangeString: String = ""            //For resource String
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
     * Generates X-axis labels for popup tooltips on weather graphs based on date range and locale.
     * Labels are either years (if it's one day repeated over many years) or short day/month names.
     *
     * @param startDate Start of the date range.
     * @param endDate End of the date range.
     * @param locale Target locale for formatting.
     * @return List of strings for popup labels.
     */
    fun generatePopUpLabels(
        startDate: LocalDate,
        endDate: LocalDate,
        locale: Locale
    ): List<String> {
        val russianMonthLabels = listOf(
            "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
        )
        val isOneDay = startDate.dayOfMonth == endDate.dayOfMonth &&
                startDate.month == endDate.month

        if (isOneDay) {
            return (startDate.year..endDate.year).map { it.toString() }
        }
        val leapYearDate = (startDate.year..endDate.year)
            .firstOrNull { Year.isLeap(it.toLong()) }
            ?.let { LocalDate.of(it, startDate.month, startDate.dayOfMonth) }
            ?: startDate

        val singleYearEndDate = endDate.minusYears((endDate.year - leapYearDate.year).toLong())
        val labels = mutableListOf<String>()
        var current = leapYearDate

        while (!current.isAfter(singleYearEndDate)) {
            val day = current.dayOfMonth
            val month = if (locale.language == "ru") {
                russianMonthLabels[current.monthValue - 1]
            } else {
                current.month.getDisplayName(TextStyle.SHORT, locale)
            }
            labels.add("$day $month")
            current = current.plusDays(1)
        }
        return labels
    }

    /**
     * Determines which kind of X-axis labels to generate based on the date range:
     * - Years if the same day is repeated.
     * - Days if range is short (less than ~3 months).
     * - Months if range is longer.
     *
     * @param startDate Start of the range.
     * @param endDate End of the range.
     * @param locale Target locale.
     * @return A list of appropriate labels for the graph's X axis.
     */
    fun generateAxisXLabels(
        startDate: LocalDate,
        endDate: LocalDate,
        locale: Locale
    ): List<String> {
        val useYearsAsLabels = startDate.dayOfMonth == endDate.dayOfMonth &&
                startDate.month == endDate.month

        val rawLabels = if (useYearsAsLabels) {
            yearLabels(startDate, endDate)
        } else {
            if (endDate.dayOfYear - startDate.withYear(endDate.year).dayOfYear <= 92) {
                dayLabels(startDate, endDate)
            } else {
                monthLabels(startDate, endDate, locale)
            }
        }
        return rawLabels
    }

    /**
     * Reduces the number of X-axis labels so they fit within the available width.
     *
     * @param rawLabels Original list of labels.
     * @param labelsWidth Total width required by all labels.
     * @param maxWidth Maximum width available for labels.
     * @return A filtered list of labels with spacing.
     */
    fun reduceAxisXLabels(
        rawLabels: List<String>,
        labelsWidth: Double,
        maxWidth: Double
    ): List<String> {
        if (labelsWidth <= maxWidth) {
            return rawLabels
        }
        val step = (labelsWidth / maxWidth).toInt().coerceAtLeast(1) + 1
        val filteredLabels = rawLabels.filterIndexed { index, _ -> index % step == 0 }
        return filteredLabels
    }

    /**
     * Generates a list of abbreviated years (e.g. `'23`, `'24`) for X-axis labels.
     */
    private fun yearLabels(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<String> {
        return (startDate.year..endDate.year).map { "'${(it % 100).toString().padStart(2, '0')}" }
    }

    /**
     * Generates a list of localized short month labels for X-axis.
     */
    private fun monthLabels(
        startDate: LocalDate,
        endDate: LocalDate,
        locale: Locale
    ): List<String> {
        val russianMonthLabels = listOf(
            "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
        )
        val singleYearEndDate = endDate.minusYears((endDate.year - startDate.year).toLong())
        val labels = mutableListOf<String>()
        var current = startDate.withDayOfMonth(1)
        while (!current.isAfter(singleYearEndDate)) {
            var label = current.month.getDisplayName(TextStyle.SHORT, locale)
            if (locale.language == "ru") {
                label = russianMonthLabels[current.monthValue - 1]
            }
            labels.add(label)
            current = current.plusMonths(1)
        }
        return labels
    }

    /**
     * Generates a list of day-of-month numbers as strings (e.g., `["1", "2", ..., "31"]`).
     */
    private fun dayLabels(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<String> {
        val leapYearDate = (startDate.year..endDate.year)
            .firstOrNull { Year.isLeap(it.toLong()) }
            ?.let { LocalDate.of(it, startDate.month, startDate.dayOfMonth) }
            ?: startDate
        val singleYearEndDate = endDate.minusYears((endDate.year - leapYearDate.year).toLong())
        return generateSequence(leapYearDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(singleYearEndDate) }
            .map { it.dayOfMonth.toString() }
            .toList()
    }
}