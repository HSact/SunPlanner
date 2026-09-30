package com.hsact.sunplanner.domain.model

/**
 * Represents the available units for measuring precipitation.
 */
enum class PrecipitationUnitMode {
    /** Millimeters (mm) */
    MM,

    /** Inches (inch) */
    INCH;

    /**
     * Converts this [PrecipitationUnitMode] to its short string representation.
     *
     * @return "mm" if [PrecipitationUnitMode.MM], "inch" if [PrecipitationUnitMode.INCH]
     */
    fun toName(): String = when (this) {
        MM -> "mm"
        INCH -> "inch"
    }

    /**
     * Converts this [PrecipitationUnitMode] to an integer index for use in UI elements (e.g., dropdowns).
     *
     * @return 0 for [PrecipitationUnitMode.MM], 1 for [PrecipitationUnitMode.INCH]
     */
    fun toIndex(): Int = when (this) {
        MM -> 0
        INCH -> 1
    }

    companion object {
        /**
         * Converts an integer index back to a [PrecipitationUnitMode] enum.
         *
         * @param index The index representing a precipitation unit (e.g., from user selection).
         * @return Corresponding [PrecipitationUnitMode], or [PrecipitationUnitMode.MM] if the index is invalid.
         */
        fun fromIndex(index: Int): PrecipitationUnitMode = when (index) {
            0 -> MM
            1 -> INCH
            else -> MM
        }
    }
}
