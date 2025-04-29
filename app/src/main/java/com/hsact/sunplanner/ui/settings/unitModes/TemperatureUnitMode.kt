package com.hsact.sunplanner.ui.settings.unitModes

enum class TemperatureUnitMode { CELSIUS, FAHRENHEIT }
fun TemperatureUnitMode.name(): String = when (this) {
    TemperatureUnitMode.CELSIUS -> "celsius"
    TemperatureUnitMode.FAHRENHEIT -> "fahrenheit"
}