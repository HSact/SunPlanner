package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.domain.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import javax.inject.Inject

class UpdateThemeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setTheme(themeMode)
    }
}