package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.domain.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode
import javax.inject.Inject

/**
 * Use case for updating the wind speed unit setting in the repository.
 *
 * @property repository The settings repository used to persist wind speed unit changes.
 */
class UpdateWindSpeedUnitUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    /**
     * Updates the wind speed unit setting.
     *
     * @param windSpeedUnit The new wind speed unit to set.
     */
    suspend operator fun invoke(windSpeedUnit: WindSpeedUnitMode) {
        repository.setWindSpeedUnit(windSpeedUnit)
    }
}