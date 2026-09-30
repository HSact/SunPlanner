package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating the application's language setting.
 *
 * @property repository Repository for managing app settings.
 */
class UpdateLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    /**
     * Updates the language mode in the settings repository.
     *
     * @param languageMode The new language mode to set.
     */
    suspend operator fun invoke(languageMode: LanguageMode) {
        repository.setLanguage(languageMode)
    }
}