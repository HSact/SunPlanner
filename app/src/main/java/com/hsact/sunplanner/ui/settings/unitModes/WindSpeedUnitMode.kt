package com.hsact.sunplanner.ui.settings.unitModes

enum class WindSpeedUnitMode { KMH, MS, MPH, KN }
fun WindSpeedUnitMode.name(): String = when (this) {
    WindSpeedUnitMode.KMH -> "kmh"
    WindSpeedUnitMode.MS -> "ms"
    WindSpeedUnitMode.MPH -> "mph"
    WindSpeedUnitMode.KN -> "kn"
}