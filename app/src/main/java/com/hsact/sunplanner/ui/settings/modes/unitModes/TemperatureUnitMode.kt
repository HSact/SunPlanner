package com.hsact.sunplanner.ui.settings.modes.unitModes

/**
 * Enum representing temperature units.
 */
enum class TemperatureUnitMode { CELSIUS, FAHRENHEIT }

/**
 * Returns the string representation of the temperature unit.
 *
 * @receiver TemperatureUnitMode enum value.
 * @return String name of the unit ("celsius" or "fahrenheit").
 */
fun TemperatureUnitMode.toName(): String = when (this) {
    TemperatureUnitMode.CELSIUS -> "celsius"
    TemperatureUnitMode.FAHRENHEIT -> "fahrenheit"
}

/**
 * Returns the index corresponding to the temperature unit.
 *
 * @receiver TemperatureUnitMode enum value.
 * @return Int index (0 for CELSIUS, 1 for FAHRENHEIT).
 */
fun TemperatureUnitMode.toIndex(): Int = when (this) {
    TemperatureUnitMode.CELSIUS -> 0
    TemperatureUnitMode.FAHRENHEIT -> 1
}

/**
 * Converts an index to the corresponding TemperatureUnitMode.
 *
 * @param index Int index value.
 * @return TemperatureUnitMode corresponding to the index. Defaults to CELSIUS for invalid indexes.
 */
fun indexToTemperatureUnitMode(index: Int): TemperatureUnitMode = when (index) {
    0 -> TemperatureUnitMode.CELSIUS
    1 -> TemperatureUnitMode.FAHRENHEIT
    else -> TemperatureUnitMode.CELSIUS
}