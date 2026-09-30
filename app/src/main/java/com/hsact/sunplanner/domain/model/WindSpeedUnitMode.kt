package com.hsact.sunplanner.domain.model

/**
 * Enum representing wind speed units.
 */
enum class WindSpeedUnitMode {
    KMH,    // Kilometers per hour
    MS,     // Meters per second
    MPH,    // Miles per hour
    KN;      // Knots

    /**
     * Returns the string representation of the wind speed unit.
     *
     * @return String name of the unit ("kmh", "ms", "mph", or "kn").
     */
    fun toName(): String = when (this) {
        KMH -> "kmh"
        MS -> "ms"
        MPH -> "mph"
        KN -> "kn"
    }

    /**
     * Returns the index corresponding to the wind speed unit.
     *
     * @return Int index (0 for KMH, 1 for MS, 2 for MPH, 3 for KN).
     */
    fun toIndex(): Int = when (this) {
        KMH -> 0
        MS -> 1
        MPH -> 2
        KN -> 3
    }

    companion object {
        /**
         * Converts an index to the corresponding WindSpeedUnitMode.
         *
         * @param index Int index value.
         * @return WindSpeedUnitMode corresponding to the index. Defaults to MS for invalid indexes.
         */
        fun fromIndex(index: Int): WindSpeedUnitMode = when (index) {
            0 -> KMH
            1 -> MS
            2 -> MPH
            3 -> KN
            else -> MS
        }
    }
}
