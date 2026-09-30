package com.hsact.sunplanner.ui.settings

import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.PrecipitationUnitMode
import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode

/**
 * UI state holder for the Settings screen.
 */
data class SettingsUIState(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val language: LanguageMode = LanguageMode.ENGLISH,
    val isDotsVisible: Boolean = true,
    val isEdgesCurved: Boolean = true,
    val temperatureUnit: TemperatureUnitMode = TemperatureUnitMode.CELSIUS,
    val windSpeedUnit: WindSpeedUnitMode = WindSpeedUnitMode.MS,
    val precipitationUnit: PrecipitationUnitMode = PrecipitationUnitMode.MM,
    val isClearCacheDialogOpen: Boolean = false,
    val appVersion: String = ""
)
