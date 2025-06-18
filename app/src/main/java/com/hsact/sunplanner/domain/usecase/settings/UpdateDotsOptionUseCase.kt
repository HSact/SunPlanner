package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateDotsOptionUseCase @Inject constructor(
private val repository: SettingsRepository
) {
    suspend operator fun invoke (dots: Int) {
        val flag = when (dots) {
            0 -> false
            else -> true
        }
        repository.setDotsVisibility(flag)
    }
}