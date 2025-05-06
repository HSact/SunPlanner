package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.data.repository.SettingsRepository
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    val theme: Flow<ThemeMode> get() = repository.theme
    val language: Flow<LanguageMode> get() = repository.language
    val location: Flow<Location?> get() = repository.location
    val temperatureUnit: Flow<TemperatureUnitMode> get() = repository.temperatureUnit
    val windUnit: Flow<WindSpeedUnitMode> get() = repository.windSpeedUnit
    val precipitationUnit: Flow<PrecipitationUnitMode> get() = repository.precipitationUnit
}