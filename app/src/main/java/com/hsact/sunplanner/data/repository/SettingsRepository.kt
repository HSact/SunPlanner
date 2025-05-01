package com.hsact.sunplanner.data.repository

import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.ThemeMode
import com.hsact.sunplanner.ui.settings.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.unitModes.WindSpeedUnitMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<ThemeMode>
    val language: Flow<LanguageMode>
    val temperatureUnit: Flow<TemperatureUnitMode>
    val windSpeedUnit: Flow<WindSpeedUnitMode>
    val precipitationUnit: Flow<PrecipitationUnitMode>
    suspend fun setTheme(themeMode: ThemeMode)
    suspend fun setLanguage(languageMode: LanguageMode)
    suspend fun setTemperatureUnit(temperatureMode: TemperatureUnitMode)
    suspend fun setWindSpeedUnit(windMode: WindSpeedUnitMode)
    suspend fun setPrecipitationUnit(precipitationMode: PrecipitationUnitMode)
}