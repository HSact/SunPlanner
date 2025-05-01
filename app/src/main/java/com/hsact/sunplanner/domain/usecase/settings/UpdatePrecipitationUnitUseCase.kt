package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.data.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.unitModes.PrecipitationUnitMode
import javax.inject.Inject

class UpdatePrecipitationUnitUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(precipitationUnit: PrecipitationUnitMode) {
        repository.setPrecipitationUnit(precipitationUnit)
    }
}