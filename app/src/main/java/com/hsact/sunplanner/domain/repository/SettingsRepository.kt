package com.hsact.sunplanner.domain.repository

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface to manage user settings.
 *
 * Provides reactive flows to observe settings changes and suspend functions
 * to update preferences.
 */
interface SettingsRepository {
    /** Flow emitting the current theme mode setting. */
    val theme: Flow<ThemeMode>

    /** Flow emitting the current language mode setting, or null if not set. */
    val language: Flow<LanguageMode?>

    /** Flow emitting the current temperature unit setting. */
    val temperatureUnit: Flow<TemperatureUnitMode>

    /** Flow emitting the current wind speed unit setting. */
    val windSpeedUnit: Flow<WindSpeedUnitMode>

    /** Flow emitting the current precipitation unit setting. */
    val precipitationUnit: Flow<PrecipitationUnitMode>

    /** Flow emitting whether dots are visible on graphs. */
    val isDotsVisible: Flow<Boolean>

    /** Flow emitting whether graphs are drawn with curved lines. */
    val isGraphCurved: Flow<Boolean>

    /** Flow emitting the saved location, or null if none. */
    val location: Flow<Location?>

    /**
     * Save the selected theme mode.
     *
     * @param themeMode Theme mode to save.
     */
    suspend fun setTheme(themeMode: ThemeMode)

    /**
     * Save the selected language mode.
     *
     * @param languageMode Language mode to save.
     */
    suspend fun setLanguage(languageMode: LanguageMode)

    /**
     * Save the selected temperature unit mode.
     *
     * @param temperatureMode Temperature unit mode to save.
     */
    suspend fun setTemperatureUnit(temperatureMode: TemperatureUnitMode)

    /**
     * Save the selected wind speed unit mode.
     *
     * @param windMode Wind speed unit mode to save.
     */
    suspend fun setWindSpeedUnit(windMode: WindSpeedUnitMode)

    /**
     * Save the selected precipitation unit mode.
     *
     * @param precipitationMode Precipitation unit mode to save.
     */
    suspend fun setPrecipitationUnit(precipitationMode: PrecipitationUnitMode)

    /**
     * Save the visibility setting for dots on graphs.
     *
     * @param isVisible True to show dots, false to hide.
     */
    suspend fun setDotsVisibility(isVisible: Boolean)

    /**
     * Save the setting for graph curvature.
     *
     * @param isCurved True to draw curved graphs, false otherwise.
     */
    suspend fun setGraphCurved(isCurved: Boolean)

    /**
     * Save the location data.
     *
     * @param location Location to save.
     */
    suspend fun setLocation(location: Location)
}