package com.hsact.sunplanner.ui.settings

import com.hsact.sunplanner.ui.settings.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.unitModes.WindSpeedUnitMode

data class SettingsUIState(
    var currentTheme: ThemeMode = ThemeMode.SYSTEM,
    var currentLanguage: LanguageMode = LanguageMode.ENGLISH,
    var selectedTheme: ThemeMode = currentTheme,
    var selectedLanguage: LanguageMode = currentLanguage,
    var currentTemperatureUnit: TemperatureUnitMode = TemperatureUnitMode.CELSIUS,
    var selectedTemperatureUnit: TemperatureUnitMode = currentTemperatureUnit,
    var currentWindSpeedUnit: WindSpeedUnitMode = WindSpeedUnitMode.MS,
    var selectedWindSpeedUnit: WindSpeedUnitMode = currentWindSpeedUnit,
    var currentPrecipitationUnit: PrecipitationUnitMode = PrecipitationUnitMode.MM,
    var selectedPrecipitationUnit: PrecipitationUnitMode = currentPrecipitationUnit
)