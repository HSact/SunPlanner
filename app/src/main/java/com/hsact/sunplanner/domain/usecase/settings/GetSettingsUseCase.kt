package com.hsact.sunplanner.domain.usecase.settings

import com.hsact.sunplanner.data.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    val theme: Flow<ThemeMode> get() = repository.theme
    val language: Flow<LanguageMode> get() = repository.language
}