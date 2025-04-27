package com.hsact.sunplanner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLanguageUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> get() = _uiState
    init {
        observeSettings()
    }
    private fun observeSettings() {
        /*getSettingsUseCase.theme.onEach { theme ->
            _uiState.value = _uiState.value.copy(currentTheme = theme, selectedTheme = theme)
        }.launchIn(viewModelScope)

        getSettingsUseCase.language.onEach { language ->
            _uiState.value = _uiState.value.copy(currentLanguage = language, selectedLanguage = language)
        }.launchIn(viewModelScope)*/
    }
    fun handleIntent(intent: SettingsIntents) {
        when (intent) {
            is SettingsIntents.UpdateTheme -> {changeTheme(intent.theme)}
            is SettingsIntents.UpdateLanguage -> {changeLanguage(intent.language)}
            is SettingsIntents.ApplySettings -> {applySettings()}
        }
    }
    private fun changeTheme(theme: ThemeMode) {
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
        //ThemeViewModel().updateTheme(theme)
    }
    private fun changeLanguage(language: LanguageMode) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }
    private fun applySettings() {
        _uiState.value = _uiState.value.copy(
            currentTheme = _uiState.value.selectedTheme,
            currentLanguage = uiState.value.selectedLanguage)
    }
}