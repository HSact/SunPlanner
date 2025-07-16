package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.domain.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import javax.inject.Inject

/**
 * Use case for updating the theme mode setting in the repository.
 *
 * @property repository The settings repository used to persist theme changes.
 */
class UpdateThemeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    /**
     * Updates the theme mode setting.
     *
     * @param themeMode The new theme mode to set.
     */
    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setTheme(themeMode)
    }
}