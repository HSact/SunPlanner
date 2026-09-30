package com.hsact.sunplanner.domain.model

/**
 * Enum representing temperature units.
 */
enum class TemperatureUnitMode {
    CELSIUS, FAHRENHEIT;

    /**
     * Returns the string representation of the temperature unit.
     *
     * @return String name of the unit ("celsius" or "fahrenheit").
     */
    fun toName(): String = when (this) {
        CELSIUS -> "celsius"
        FAHRENHEIT -> "fahrenheit"
    }

    /**
     * Returns the index corresponding to the temperature unit.
     *
     * @return Int index (0 for CELSIUS, 1 for FAHRENHEIT).
     */
    fun toIndex(): Int = when (this) {
        CELSIUS -> 0
        FAHRENHEIT -> 1
    }

    companion object {
        /**
         * Converts an index to the corresponding TemperatureUnitMode.
         *
         * @param index Int index value.
         * @return TemperatureUnitMode corresponding to the index. Defaults to CELSIUS for invalid indexes.
         */
        fun fromIndex(index: Int): TemperatureUnitMode = when (index) {
            0 -> CELSIUS
            1 -> FAHRENHEIT
            else -> CELSIUS
        }
    }
}
