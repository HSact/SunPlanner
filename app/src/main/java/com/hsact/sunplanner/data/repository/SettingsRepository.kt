package com.hsact.sunplanner.data.repository

import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<ThemeMode>
    val language: Flow<LanguageMode>
    suspend fun setTheme(themeMode: ThemeMode)
    suspend fun setLanguage(languageMode: LanguageMode)
}