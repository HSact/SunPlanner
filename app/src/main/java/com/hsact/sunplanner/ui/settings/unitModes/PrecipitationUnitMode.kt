package com.hsact.sunplanner.ui.settings.unitModes

enum class PrecipitationUnitMode { MM, INCH }
fun PrecipitationUnitMode.name(): String = when (this) {
    PrecipitationUnitMode.MM -> "mm"
    PrecipitationUnitMode.INCH -> "inch"
}