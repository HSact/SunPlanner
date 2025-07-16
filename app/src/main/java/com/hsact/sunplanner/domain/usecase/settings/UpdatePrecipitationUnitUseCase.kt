package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.domain.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import javax.inject.Inject

/**
 * Use case for updating the precipitation unit setting in the repository.
 *
 * @property repository The settings repository used to persist changes.
 */
class UpdatePrecipitationUnitUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    /**
     * Updates the precipitation unit setting.
     *
     * @param precipitationUnit The new precipitation unit to set.
     */
    suspend operator fun invoke(precipitationUnit: PrecipitationUnitMode) {
        repository.setPrecipitationUnit(precipitationUnit)
    }
}