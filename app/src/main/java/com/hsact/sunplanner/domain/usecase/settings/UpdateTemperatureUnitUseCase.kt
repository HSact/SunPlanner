package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating the temperature unit setting in the repository.
 *
 * @property repository The settings repository used to persist changes.
 */
class UpdateTemperatureUnitUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    /**
     * Updates the temperature unit setting.
     *
     * @param temperatureUnit The new temperature unit to set.
     */
    suspend operator fun invoke(temperatureUnit: TemperatureUnitMode) {
        repository.setTemperatureUnit(temperatureUnit)
    }
}