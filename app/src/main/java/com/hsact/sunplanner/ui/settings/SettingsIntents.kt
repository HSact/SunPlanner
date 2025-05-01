package com.hsact.sunplanner.ui.settings

import com.hsact.sunplanner.ui.settings.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.unitModes.WindSpeedUnitMode

sealed class SettingsIntents {
    data class UpdateTheme(val theme: ThemeMode) : SettingsIntents()
    data class UpdateLanguage(val language: LanguageMode) : SettingsIntents()
    data class UpdateTemperatureUnit(val unitTemp: TemperatureUnitMode) : SettingsIntents()
    data class UpdateWindSpeedUnit(val unitWind: WindSpeedUnitMode) : SettingsIntents()
    data class UpdatePrecipitationUnit(val unitPrecipitation: PrecipitationUnitMode) : SettingsIntents()
    object ApplySettings : SettingsIntents()
}