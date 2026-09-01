package com.hsact.sunplanner.ui.settings

import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.PrecipitationUnitMode
import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode

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