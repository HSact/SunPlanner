package com.hsact.sunplanner.ui.settings

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLanguageUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
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
        viewModelScope.launch {
            observeSettings()
        }
    }
    private suspend fun observeSettings() {
        /*getSettingsUseCase.theme.collect {theme: ThemeMode ->
            _uiState.value = _uiState.value.copy(currentTheme = theme)
        }

        getSettingsUseCase.language.collect { language: LanguageMode ->
            _uiState.value = _uiState.value.copy(currentLanguage = language)
        }*/
        viewModelScope.launch {
            getSettingsUseCase.theme.collect { theme ->
                _uiState.value = _uiState.value.copy(currentTheme = theme)
            }
        }
        viewModelScope.launch {
            getSettingsUseCase.language.collect { language ->
                _uiState.value = _uiState.value.copy(currentLanguage = language)
            }
        }
    }
    fun handleIntent(intent: SettingsIntents) {
        viewModelScope.launch {
            when (intent) {
                is SettingsIntents.UpdateTheme -> {changeTheme(intent.theme)}
                is SettingsIntents.UpdateLanguage -> {changeLanguage(intent.language)}
                is SettingsIntents.ApplySettings -> {applySettings()}
            }
        }
    }
    private suspend fun changeTheme(theme: ThemeMode) {
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
        updateThemeUseCase(theme)
        //ThemeViewModel().updateTheme(theme)
    }
    private suspend fun changeLanguage(language: LanguageMode) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }
    private suspend fun applySettings() {
        _uiState.value = _uiState.value.copy(
            currentTheme = _uiState.value.selectedTheme,
            currentLanguage = uiState.value.selectedLanguage)
        updateLanguageUseCase(_uiState.value.selectedLanguage)
        val locale = when (_uiState.value.selectedLanguage) {
            LanguageMode.ENGLISH -> Locale.ENGLISH
            LanguageMode.RUSSIAN -> Locale("ru")
        }
        Locale.setDefault(locale)
        val config = Resources.getSystem().configuration
        config.setLocale(locale)
    }
}