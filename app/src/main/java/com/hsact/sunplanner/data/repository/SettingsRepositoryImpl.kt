package com.hsact.sunplanner.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    companion object {
        private val THEME_KEY = intPreferencesKey("theme")
        private val LANGUAGE_KEY = intPreferencesKey("language")
    }

    override val theme: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY]?.let { ThemeMode.entries[it] } ?: ThemeMode.SYSTEM
    }

    override val language: Flow<LanguageMode> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]?.let { LanguageMode.entries[it] } ?: LanguageMode.ENGLISH
    }

    override suspend fun setTheme(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.ordinal
        }
    }

    override suspend fun setLanguage(languageMode: LanguageMode) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageMode.ordinal
        }
    }
}