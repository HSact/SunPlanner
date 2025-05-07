package com.hsact.sunplanner.ui.settings

import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode

data class SettingsUIState(
    var currentTheme: ThemeMode = ThemeMode.SYSTEM,
    var selectedTheme: ThemeMode = currentTheme,
    var currentLanguage: LanguageMode = LanguageMode.ENGLISH,
    var selectedLanguage: LanguageMode = currentLanguage,
    var currentTemperatureUnit: TemperatureUnitMode = TemperatureUnitMode.CELSIUS,
    var selectedTemperatureUnit: TemperatureUnitMode = currentTemperatureUnit,
    var currentWindSpeedUnit: WindSpeedUnitMode = WindSpeedUnitMode.MS,
    var selectedWindSpeedUnit: WindSpeedUnitMode = currentWindSpeedUnit,
    var currentPrecipitationUnit: PrecipitationUnitMode = PrecipitationUnitMode.MM,
    var selectedPrecipitationUnit: PrecipitationUnitMode = currentPrecipitationUnit
)