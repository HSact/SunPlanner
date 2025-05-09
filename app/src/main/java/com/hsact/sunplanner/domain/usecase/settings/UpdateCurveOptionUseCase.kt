package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.data.repository.SettingsRepository
import javax.inject.Inject

class UpdateCurveOptionUseCase  @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke (isCurved: Int) {
        val flag = when (isCurved) {
            0 -> false
            else -> true
        }
        repository.setGraphCurved(flag)
    }
}