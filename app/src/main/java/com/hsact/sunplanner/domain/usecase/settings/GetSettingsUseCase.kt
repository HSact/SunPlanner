package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving application settings as reactive flows.
 *
 * Provides access to theme, language, units, location, and display preferences.
 *
 * @property repository The repository providing access to stored settings.
 */
class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    /** Flow emitting current theme mode. */
    val theme: Flow<ThemeMode> get() = repository.theme

    /** Flow emitting current language mode or null if not set. */
    val language: Flow<LanguageMode?> get() = repository.language

    /** Flow emitting boolean indicating whether dots are visible in graphs. */
    val isDotsVisible: Flow<Boolean> get() = repository.isDotsVisible

    /** Flow emitting boolean indicating whether graph edges are curved. */
    val isEdgesCurved: Flow<Boolean> get() = repository.isGraphCurved

    /** Flow emitting the saved location or null if none set. */
    val location: Flow<Location?> get() = repository.location

    /** Flow emitting current temperature unit mode. */
    val temperatureUnit: Flow<TemperatureUnitMode> get() = repository.temperatureUnit

    /** Flow emitting current wind speed unit mode. */
    val windUnit: Flow<WindSpeedUnitMode> get() = repository.windSpeedUnit

    /** Flow emitting current precipitation unit mode. */
    val precipitationUnit: Flow<PrecipitationUnitMode> get() = repository.precipitationUnit
}