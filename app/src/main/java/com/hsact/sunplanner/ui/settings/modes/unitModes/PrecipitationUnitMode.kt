package com.hsact.sunplanner.ui.settings.modes.unitModes

/**
 * Represents the available units for measuring precipitation.
 */
enum class PrecipitationUnitMode {
    /** Millimeters (mm) */
    MM,

    /** Inches (inch) */
    INCH
}

/**
 * Converts this [PrecipitationUnitMode] to its short string representation.
 *
 * @return "mm" if [PrecipitationUnitMode.MM], "inch" if [PrecipitationUnitMode.INCH]
 */
fun PrecipitationUnitMode.toName(): String = when (this) {
    PrecipitationUnitMode.MM -> "mm"
    PrecipitationUnitMode.INCH -> "inch"
}

/**
 * Converts this [PrecipitationUnitMode] to an integer index for use in UI elements (e.g., dropdowns).
 *
 * @return 0 for [PrecipitationUnitMode.MM], 1 for [PrecipitationUnitMode.INCH]
 */
fun PrecipitationUnitMode.toIndex(): Int = when (this) {
    PrecipitationUnitMode.MM -> 0
    PrecipitationUnitMode.INCH -> 1
}

/**
 * Converts an integer index back to a [PrecipitationUnitMode] enum.
 *
 * @param index The index representing a precipitation unit (e.g., from user selection).
 * @return Corresponding [PrecipitationUnitMode], or [PrecipitationUnitMode.MM] if the index is invalid.
 */
fun indexToPrecipitationUnitMode(index: Int): PrecipitationUnitMode = when (index) {
    0 -> PrecipitationUnitMode.MM
    1 -> PrecipitationUnitMode.INCH
    else -> PrecipitationUnitMode.MM
}