package com.hsact.sunplanner.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> get() = _uiState
    fun handleIntent(intent: SettingsIntents) {
        when (intent) {
            is SettingsIntents.UpdateTheme -> {changeTheme(intent.theme)}
            is SettingsIntents.UpdateLanguage -> {changeLanguage(intent.language)}
            is SettingsIntents.ApplySettings -> {applySettings()}
        }
    }
    private fun changeTheme(theme: ThemeMode) {
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
    }
    private fun changeLanguage(language: LanguageMode) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }
    private fun applySettings() {
        TODO("Not yet implemented")
    }
}