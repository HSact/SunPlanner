package com.hsact.sunplanner.data.utils

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {
    fun formatDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        isOneDay: Boolean
    ): String {
        return if (isOneDay) {
            val monthName = startDate.month
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercase() }
            "${startDate.dayOfMonth} of $monthName in ${startDate.year}-${endDate.year} values"
        } else {
            val monthName1 = startDate.month
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercase() }
            val monthName2 = endDate.month
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercase() }
            "${startDate.dayOfMonth} $monthName1 - ${endDate.dayOfMonth} $monthName2 average in ${startDate.year}-${endDate.year} values"
        }
    }

    fun labelsForCard(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<String> {
        val useYearsAsLabels = startDate.dayOfMonth == endDate.dayOfMonth &&
                startDate.month == endDate.month

        val rawLabels = if (useYearsAsLabels) {
            (startDate.year..endDate.year).map {
                "'${(it % 100).toString().padStart(2, '0')}"
            }
        } else {
            val singleYearEndDate = endDate.minusYears((endDate.year - startDate.year).toLong())
            generateSequence(startDate) { it.plusDays(1) }
                .takeWhile { !it.isAfter(singleYearEndDate) }
                .map { it.dayOfMonth.toString() }
                .toList()
        }
        if (rawLabels.size <= 20) return rawLabels
        val step = (rawLabels.size / 20.0).toInt().coerceAtLeast(1) + 1
        val filteredLabels = rawLabels.filterIndexed { index, _ -> index % step == 0 }
        return filteredLabels
    }
}