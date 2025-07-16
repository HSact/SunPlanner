package com.hsact.sunplanner.domain.model

import java.time.LocalDate

/**
 * Data class representing a date range with a start and end date.
 *
 * @property startDate The start date of the range.
 * @property endDate The end date of the range.
 */
data class DatesBundle(
    var startDate: LocalDate,
    var endDate: LocalDate
) {

    /**
     * Checks if the start year is not after the end year.
     *
     * @return `true` if [startDate] year is less than or equal to [endDate] year, `false` otherwise.
     */
    fun isStartYearNotAfterEndYear(): Boolean {
        return startDate.year <= endDate.year
    }

    /**
     * Checks if the date range is valid within the same year.
     * Compares the day of year of [startDate] adjusted to [endDate] year with the day of year of [endDate].
     *
     * @return `true` if the adjusted start date is before or equal to the end date, `false` otherwise.
     */
    fun isDateRangeValid(): Boolean {
        return startDate.withYear(endDate.year).dayOfYear <= endDate.dayOfYear
    }

    /**
     * Checks if the difference between the end year and start year is within a given limit.
     *
     * @param maxYearRange The maximum allowed difference in years.
     * @return `true` if the year difference is less than [maxYearRange], `false` otherwise.
     */
    fun isYearsRangeWithinLimit(maxYearRange: Int): Boolean {
        return (endDate.year - startDate.year) < maxYearRange
    }
}