package com.hsact.sunplanner.ui.settings

import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.PrecipitationUnitMode
import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode

/**
 * Sealed class representing user intents for the Settings screen.
 */
sealed class SettingsIntents {
    /**
     * Intent to update the application theme.
     * @param theme The selected [ThemeMode].
     */
    data class UpdateTheme(val theme: ThemeMode) : SettingsIntents()

    /**
     * Intent to update the application language.
     * @param language The selected [LanguageMode].
     */
    data class UpdateLanguage(val language: LanguageMode) : SettingsIntents()

    /**
     * Intent to update whether dots are visible on the weather graph.
     * @param isVisible True if dots should be shown.
     */
    data class UpdateDotsOption(val isVisible: Boolean) : SettingsIntents()

    /**
     * Intent to update whether the weather graph uses curved edges.
     * @param isCurved True if edges should be curved.
     */
    data class UpdateCurveOption(val isCurved: Boolean) : SettingsIntents()

    /**
     * Intent to update the temperature unit.
     * @param unitTemp The selected [TemperatureUnitMode].
     */
    data class UpdateTemperatureUnit(val unitTemp: TemperatureUnitMode) : SettingsIntents()

    /**
     * Intent to update the wind speed unit.
     * @param unitWind The selected [WindSpeedUnitMode].
     */
    data class UpdateWindSpeedUnit(val unitWind: WindSpeedUnitMode) : SettingsIntents()

    /**
     * Intent to update the precipitation unit.
     * @param unitPrecipitation The selected [PrecipitationUnitMode].
     */
    data class UpdatePrecipitationUnit(val unitPrecipitation: PrecipitationUnitMode) :
        SettingsIntents()

    /**
     * Intent to show or hide the clear cache confirmation dialog.
     * @param visible True to show the dialog.
     */
    data class SetClearCacheDialogVisible(val visible: Boolean) : SettingsIntents()

    /**
     * Intent to clear all cached weather data and favorites.
     */
    object ClearCache : SettingsIntents()
}
