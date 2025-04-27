package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.data.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.LanguageMode
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(languageMode: LanguageMode) {
        repository.setLanguage(languageMode)
    }
}