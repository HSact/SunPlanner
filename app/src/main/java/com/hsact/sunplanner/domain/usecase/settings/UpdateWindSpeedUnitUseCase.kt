package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.data.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.unitModes.WindSpeedUnitMode
import javax.inject.Inject

class UpdateWindSpeedUnitUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(windSpeedUnit: WindSpeedUnitMode) {
        repository.setWindSpeedUnit(windSpeedUnit)
    }
}