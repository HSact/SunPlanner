package com.hsact.sunplanner.domain.model

import java.time.LocalDate

/**
 * Data class representing a date range with a start and end date.
 *
 * @property start The start date of the range.
 * @property end The end date of the range.
 */
data class DatesBundle(
    val start: LocalDate,
    val end: LocalDate
) {

    /**
     * Checks if the start year is not after the end year.
     *
     * @return `true` if [start] year is less than or equal to [end] year, `false` otherwise.
     */
    val isStartYearNotAfterEndYear: Boolean
        get() = start.year <= end.year

    /**
     * Checks if the difference between the end year and start year is within a given limit.
     *
     * @param maxYearRange The maximum allowed difference in years.
     * @return `true` if the year difference is less than [maxYearRange], `false` otherwise.
     */
    fun isYearsRangeWithinLimit(maxYearRange: Int): Boolean {
        return (end.year - start.year) < maxYearRange
    }
}