package com.hsact.sunplanner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLanguageUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdatePrecipitationUnitUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateTemperatureUnitUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateThemeUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateWindSpeedUnitUseCase
import com.hsact.sunplanner.ui.settings.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.unitModes.WindSpeedUnitMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateTemperatureUnitUseCase: UpdateTemperatureUnitUseCase,
    private val updateWindSpeedUnitUseCase: UpdateWindSpeedUnitUseCase,
    private val updatePrecipitationUnitUseCase: UpdatePrecipitationUnitUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> get() = _uiState
    init {
        viewModelScope.launch {
            observeSettings()
        }
    }
    private fun observeSettings() {
        viewModelScope.launch {
            getSettingsUseCase.theme.collect { theme ->
                _uiState.value = _uiState.value.copy(currentTheme = theme)
                _uiState.value = _uiState.value.copy(selectedTheme = theme)
            }
        }
        viewModelScope.launch {
            getSettingsUseCase.language.collect { language ->
                _uiState.value = _uiState.value.copy(currentLanguage = language)
                _uiState.value = _uiState.value.copy(selectedLanguage = language)
            }
        }
    }
    fun handleIntent(intent: SettingsIntents) {
        viewModelScope.launch {
            when (intent) {
                is SettingsIntents.UpdateTheme -> {changeTheme(intent.theme)}
                is SettingsIntents.UpdateLanguage -> {changeLanguage(intent.language)}
                is SettingsIntents.UpdateTemperatureUnit -> {changeTemperatureUnit(intent.unitTemp)}
                is SettingsIntents.UpdateWindSpeedUnit -> {changeWindSpeedUnit(intent.unitWind)}
                is SettingsIntents.UpdatePrecipitationUnit -> {changePrecipitationUnit(intent.unitPrecipitation)}
                is SettingsIntents.ApplySettings -> {applySettings()}
            }
        }
    }

    private fun changeTemperatureUnit(temperatureUnit: TemperatureUnitMode) {
        _uiState.value = _uiState.value.copy(selectedTemperatureUnit = temperatureUnit)
    }
    private fun changeWindSpeedUnit(windSpeedUnit: WindSpeedUnitMode) {
        _uiState.value = _uiState.value.copy(selectedWindSpeedUnit = windSpeedUnit)
    }
    private fun changePrecipitationUnit(precipitationUnit: PrecipitationUnitMode) {
        _uiState.value = _uiState.value.copy(selectedPrecipitationUnit = precipitationUnit)
    }

    private suspend fun changeTheme(theme: ThemeMode) {
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
        updateThemeUseCase(theme)
    }
    private fun changeLanguage(language: LanguageMode) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }
    private suspend fun applySettings() {
        updateTemperatureUnitUseCase(_uiState.value.selectedTemperatureUnit)
        updateWindSpeedUnitUseCase(_uiState.value.selectedWindSpeedUnit)
        updatePrecipitationUnitUseCase(_uiState.value.selectedPrecipitationUnit)
        _uiState.value = _uiState.value.copy(
            currentTheme = _uiState.value.selectedTheme)
        if (_uiState.value.currentLanguage != _uiState.value.selectedLanguage) {
            _uiState.value = _uiState.value.copy(currentLanguage = _uiState.value.selectedLanguage)
            updateLanguageUseCase(_uiState.value.selectedLanguage)
        }
    }
}