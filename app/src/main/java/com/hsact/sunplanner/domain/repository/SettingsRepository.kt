package com.hsact.sunplanner.domain.repository

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<ThemeMode>
    val language: Flow<LanguageMode?>
    val temperatureUnit: Flow<TemperatureUnitMode>
    val windSpeedUnit: Flow<WindSpeedUnitMode>
    val precipitationUnit: Flow<PrecipitationUnitMode>
    val isDotsVisible: Flow<Boolean>
    val isGraphCurved: Flow<Boolean>
    val location: Flow<Location?>
    suspend fun setTheme(themeMode: ThemeMode)
    suspend fun setLanguage(languageMode: LanguageMode)
    suspend fun setTemperatureUnit(temperatureMode: TemperatureUnitMode)
    suspend fun setWindSpeedUnit(windMode: WindSpeedUnitMode)
    suspend fun setPrecipitationUnit(precipitationMode: PrecipitationUnitMode)
    suspend fun setDotsVisibility(isVisible: Boolean)
    suspend fun setGraphCurved(isCurved: Boolean)
    suspend fun setLocation(location: Location)
}