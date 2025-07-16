package com.hsact.sunplanner.ui.settings

import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode

sealed class SettingsIntents {
    data class UpdateTheme(val theme: ThemeMode) : SettingsIntents()
    data class UpdateLanguage(val language: LanguageMode) : SettingsIntents()
    data class UpdateDotsOption(val dots: Int) : SettingsIntents()
    data class UpdateCurveOption(val curve: Int) : SettingsIntents()
    data class UpdateTemperatureUnit(val unitTemp: TemperatureUnitMode) : SettingsIntents()
    data class UpdateWindSpeedUnit(val unitWind: WindSpeedUnitMode) : SettingsIntents()
    data class UpdatePrecipitationUnit(val unitPrecipitation: PrecipitationUnitMode) :
        SettingsIntents()

    object ApplySettings : SettingsIntents()
}