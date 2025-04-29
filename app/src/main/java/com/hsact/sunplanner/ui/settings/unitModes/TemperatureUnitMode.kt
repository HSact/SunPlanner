package com.hsact.sunplanner.ui.settings.unitModes

enum class TemperatureUnitMode { CELSIUS, FAHRENHEIT }
fun TemperatureUnitMode.name(): String = when (this) {
    TemperatureUnitMode.CELSIUS -> "celsius"
    TemperatureUnitMode.FAHRENHEIT -> "fahrenheit"
}
fun TemperatureUnitMode.toIndex(): Int = when (this) {
    TemperatureUnitMode.CELSIUS -> 0
    TemperatureUnitMode.FAHRENHEIT -> 1
}
fun indexToTemperatureUnitMode(index: Int): TemperatureUnitMode = when (index) {
    0 -> TemperatureUnitMode.CELSIUS
    1 -> TemperatureUnitMode.FAHRENHEIT
    else -> TemperatureUnitMode.CELSIUS
}