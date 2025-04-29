package com.hsact.sunplanner.ui.settings

sealed class SettingsIntents {
    data class UpdateTheme(val theme: ThemeMode) : SettingsIntents()
    data class UpdateLanguage(val language: LanguageMode) : SettingsIntents()
    data class UpdateTemperatureUnit(val unitTemp: LanguageMode) : SettingsIntents()
    data class UpdateWindSpeedUnit(val unitWind: LanguageMode) : SettingsIntents()
    data class UpdatePrecipitationUnit(val unitPrecipitation: LanguageMode) : SettingsIntents()
    object ApplySettings : SettingsIntents()
}