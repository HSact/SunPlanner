package com.hsact.sunplanner.domain.model

import com.hsact.sunplanner.data.responses.Location
import kotlinx.serialization.Serializable

/**
 * Data class representing a saved weather search (Bookmark).
 * Includes the location and the specific date range.
 */
@Serializable
data class Bookmark(
    val id: String,
    val location: Location,
    val dates: DatesBundleSerializable
)
