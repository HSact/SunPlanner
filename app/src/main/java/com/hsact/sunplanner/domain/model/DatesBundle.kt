package com.hsact.sunplanner.domain.model

import java.time.LocalDate

data class DatesBundle(
    var startDate: LocalDate,
    var endDate: LocalDate
) {
    fun isStartYearNotAfterEndYear(): Boolean {
        return startDate.year <= endDate.year
    }

    fun isDateRangeValid(): Boolean {
        return startDate.withYear(endDate.year).dayOfYear <= endDate.dayOfYear
    }

    fun isYearsRangeWithinLimit(maxYearRange: Int): Boolean {
        return (endDate.year - startDate.year) < maxYearRange
    }
}