package com.hsact.sunplanner.data.utils

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {
    fun formatDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        isOneDay: Boolean,
        isOneYear: Boolean,
        locale: Locale = Locale.getDefault(),
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

    fun labelsForCard(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<String> {
        val useYearsAsLabels = startDate.dayOfMonth == endDate.dayOfMonth &&
                startDate.month == endDate.month

        val rawLabels = if (useYearsAsLabels) {
            yearLabels(startDate, endDate)
        } else {
            if (endDate.dayOfYear - startDate.withYear(endDate.year).dayOfYear <= 92) {
                dayLabels(startDate, endDate)
            }
            else {
                monthLabels(startDate, endDate)
            }
        }
        if (rawLabels.size <= 10) return rawLabels
        val step = (rawLabels.size / 10.0).toInt().coerceAtLeast(2)
        val filteredLabels = rawLabels.filterIndexed { index, _ -> index % step == 0 }
        return filteredLabels
    }

    private fun yearLabels(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<String> {
        return (startDate.year..endDate.year).map { "'${(it % 100).toString().padStart(2, '0')}" }
    }

    private fun monthLabels(startDate: LocalDate, endDate: LocalDate): List<String> {
        val locale = Locale.getDefault()
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

    private fun dayLabels(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<String> {
        val singleYearEndDate = endDate.minusYears((endDate.year - startDate.year).toLong())
        return generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(singleYearEndDate) }
            .map { it.dayOfMonth.toString() }
            .toList()
    }
}