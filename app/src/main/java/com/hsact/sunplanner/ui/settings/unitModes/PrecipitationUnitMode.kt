package com.hsact.sunplanner.ui.settings.unitModes

enum class PrecipitationUnitMode { MM, INCH }
fun PrecipitationUnitMode.name(): String = when (this) {
    PrecipitationUnitMode.MM -> "mm"
    PrecipitationUnitMode.INCH -> "inch"
}
fun PrecipitationUnitMode.toIndex(): Int = when (this) {
    PrecipitationUnitMode.MM -> 0
    PrecipitationUnitMode.INCH -> 1
}
fun indexToPrecipitationUnitMode(index: Int): PrecipitationUnitMode = when (index) {
    0 -> PrecipitationUnitMode.MM
    1 -> PrecipitationUnitMode.INCH
    else -> PrecipitationUnitMode.MM
}