package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(location: Location) {
        repository.setLocation(location)
    }
}