package com.hsact.sunplanner.domain.model

import com.hsact.sunplanner.data.responses.Location

/**
 * Data class that aggregates user settings for the application.
 *
 * @property location The selected geographic location, or null if not set.
 * @property isDotsVisible Flag indicating whether dots are visible on graphs.
 * @property isEdgesCurved Flag indicating whether graph edges are curved.
 * @property languageMode The selected language mode.
 * @property themeMode The selected theme mode (light/dark/system).
 * @property temperatureUnitMode The selected temperature unit.
 * @property windUnitMode The selected wind speed unit.
 * @property precipitationUnitMode The selected precipitation unit.
 */
data class SettingsBundle(
    val location: Location? = null,
    val isDotsVisible: Boolean = true,
    val isEdgesCurved: Boolean = true,
    val languageMode: LanguageMode = LanguageMode.ENGLISH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val temperatureUnitMode: TemperatureUnitMode = TemperatureUnitMode.CELSIUS,
    val windUnitMode: WindSpeedUnitMode = WindSpeedUnitMode.MS,
    val precipitationUnitMode: PrecipitationUnitMode = PrecipitationUnitMode.MM
)