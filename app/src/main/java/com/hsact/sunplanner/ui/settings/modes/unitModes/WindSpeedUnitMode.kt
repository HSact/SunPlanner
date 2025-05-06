package com.hsact.sunplanner.ui.settings.modes.unitModes

enum class WindSpeedUnitMode { KMH, MS, MPH, KN }
fun WindSpeedUnitMode.toName(): String = when (this) {
    WindSpeedUnitMode.KMH -> "kmh"
    WindSpeedUnitMode.MS -> "ms"
    WindSpeedUnitMode.MPH -> "mph"
    WindSpeedUnitMode.KN -> "kn"
}
fun WindSpeedUnitMode.toIndex(): Int = when (this) {
    WindSpeedUnitMode.KMH -> 0
    WindSpeedUnitMode.MS -> 1
    WindSpeedUnitMode.MPH -> 2
    WindSpeedUnitMode.KN -> 3
}
fun indexToWindSpeedUnitMode(index: Int): WindSpeedUnitMode = when (index) {
    0 -> WindSpeedUnitMode.KMH
    1 -> WindSpeedUnitMode.MS
    2 -> WindSpeedUnitMode.MPH
    3 -> WindSpeedUnitMode.KN
    else -> WindSpeedUnitMode.MS
}