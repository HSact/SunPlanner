package com.hsact.sunplanner.ui.settings

import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode

/**
 * UI state holder for the Settings screen.
 *
 * Keeps track of both the current (saved) and selected (temporary) values for all settings,
 * allowing the user to preview changes before applying them.
 *
 * @property currentTheme The theme mode currently saved in preferences.
 * @property selectedTheme The theme mode currently selected by the user in UI.
 * @property currentLanguage The language currently saved in preferences.
 * @property selectedLanguage The language currently selected by the user in UI.
 * @property currentDotsOption Whether dots on the graph are currently visible (0 = off, 1 = on).
 * @property selectedDotsOption Whether dots should be visible based on current selection.
 * @property currentCurvedOption Whether graph edges are currently curved (0 = off, 1 = on).
 * @property selectedCurvedOption Whether graph edges should be curved based on selection.
 * @property currentTemperatureUnit Current temperature unit (°C or °F).
 * @property selectedTemperatureUnit Selected temperature unit in the UI.
 * @property currentWindSpeedUnit Current wind speed unit (km/h, m/s, etc.).
 * @property selectedWindSpeedUnit Selected wind speed unit in the UI.
 * @property currentPrecipitationUnit Current precipitation unit (mm or inches).
 * @property selectedPrecipitationUnit Selected precipitation unit in the UI.
 */
data class SettingsUIState(
    val currentTheme: ThemeMode = ThemeMode.SYSTEM,
    val selectedTheme: ThemeMode = currentTheme,
    val currentLanguage: LanguageMode = LanguageMode.ENGLISH,
    val selectedLanguage: LanguageMode = currentLanguage,
    val currentDotsOption: Int = 1,
    val selectedDotsOption: Int = currentDotsOption,
    val currentCurvedOption: Int = 1,
    val selectedCurvedOption: Int = currentCurvedOption,
    val currentTemperatureUnit: TemperatureUnitMode = TemperatureUnitMode.CELSIUS,
    val selectedTemperatureUnit: TemperatureUnitMode = currentTemperatureUnit,
    val currentWindSpeedUnit: WindSpeedUnitMode = WindSpeedUnitMode.MS,
    val selectedWindSpeedUnit: WindSpeedUnitMode = currentWindSpeedUnit,
    val currentPrecipitationUnit: PrecipitationUnitMode = PrecipitationUnitMode.MM,
    val selectedPrecipitationUnit: PrecipitationUnitMode = currentPrecipitationUnit
)