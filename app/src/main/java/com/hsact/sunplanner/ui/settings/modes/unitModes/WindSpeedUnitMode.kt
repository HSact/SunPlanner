package com.hsact.sunplanner.ui.settings.modes.unitModes

/**
 * Enum representing wind speed units.
 */
enum class WindSpeedUnitMode {
    KMH,    // Kilometers per hour
    MS,     // Meters per second
    MPH,    // Miles per hour
    KN      // Knots
}

/**
 * Returns the string representation of the wind speed unit.
 *
 * @receiver WindSpeedUnitMode enum value.
 * @return String name of the unit ("kmh", "ms", "mph", or "kn").
 */
fun WindSpeedUnitMode.toName(): String = when (this) {
    WindSpeedUnitMode.KMH -> "kmh"
    WindSpeedUnitMode.MS -> "ms"
    WindSpeedUnitMode.MPH -> "mph"
    WindSpeedUnitMode.KN -> "kn"
}

/**
 * Returns the index corresponding to the wind speed unit.
 *
 * @receiver WindSpeedUnitMode enum value.
 * @return Int index (0 for KMH, 1 for MS, 2 for MPH, 3 for KN).
 */
fun WindSpeedUnitMode.toIndex(): Int = when (this) {
    WindSpeedUnitMode.KMH -> 0
    WindSpeedUnitMode.MS -> 1
    WindSpeedUnitMode.MPH -> 2
    WindSpeedUnitMode.KN -> 3
}

/**
 * Converts an index to the corresponding WindSpeedUnitMode.
 *
 * @param index Int index value.
 * @return WindSpeedUnitMode corresponding to the index. Defaults to MS for invalid indexes.
 */
fun indexToWindSpeedUnitMode(index: Int): WindSpeedUnitMode = when (index) {
    0 -> WindSpeedUnitMode.KMH
    1 -> WindSpeedUnitMode.MS
    2 -> WindSpeedUnitMode.MPH
    3 -> WindSpeedUnitMode.KN
    else -> WindSpeedUnitMode.MS
}