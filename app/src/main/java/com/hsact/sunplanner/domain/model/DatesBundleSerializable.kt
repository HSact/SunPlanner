package com.hsact.sunplanner.domain.model

import kotlinx.serialization.Serializable

/**
 * A serializable version of DatesBundle to be used in Bookmarks.
 */
@Serializable
data class DatesBundleSerializable(
    val startYear: Int,
    val startMonth: Int,
    val startDay: Int,
    val endYear: Int,
    val endMonth: Int,
    val endDay: Int
)