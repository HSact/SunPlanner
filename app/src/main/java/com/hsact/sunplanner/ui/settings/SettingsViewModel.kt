package com.hsact.sunplanner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateDotsOptionUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLanguageUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdatePrecipitationUnitUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateTemperatureUnitUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateThemeUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateWindSpeedUnitUseCase
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.nameToLanguageMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateDotsOptionUseCase: UpdateDotsOptionUseCase,
    private val updateTemperatureUnitUseCase: UpdateTemperatureUnitUseCase,
    private val updateWindSpeedUnitUseCase: UpdateWindSpeedUnitUseCase,
    private val updatePrecipitationUnitUseCase: UpdatePrecipitationUnitUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> get() = _uiState

    init {
        viewModelScope.launch {
            observeSettings()
        }
    }

    private data class UnitsData(
        val temperatureUnit: TemperatureUnitMode,
        val windUnit: WindSpeedUnitMode,
        val precipitationUnit: PrecipitationUnitMode
    )

    private fun observeSettings() {
        viewModelScope.launch {
            val unitsFlow = combine(
                getSettingsUseCase.temperatureUnit,
                getSettingsUseCase.windUnit,
                getSettingsUseCase.precipitationUnit
            ) { temperatureUnit, windUnit, precipitationUnit ->
                UnitsData(temperatureUnit, windUnit, precipitationUnit)
            }

            combine(
                getSettingsUseCase.theme,
                getSettingsUseCase.language,
                unitsFlow,
                getSettingsUseCase.isDotsVisible
            ) { theme, language, units, showDots ->
                _uiState.value.copy(
                    currentTheme = theme,
                    currentLanguage = language ?: nameToLanguageMode(Locale.getDefault().language),
                    currentTemperatureUnit = units.temperatureUnit,
                    currentWindSpeedUnit = units.windUnit,
                    currentPrecipitationUnit = units.precipitationUnit,
                    currentDotsOption = if (showDots) 1 else 0,
                    selectedLanguage = language ?: nameToLanguageMode(Locale.getDefault().language),
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun handleIntent(intent: SettingsIntents) {
        viewModelScope.launch {
            when (intent) {
                is SettingsIntents.UpdateTheme -> {
                    changeTheme(intent.theme)
                }

                is SettingsIntents.UpdateLanguage -> {
                    changeLanguage(intent.language)
                }

                is SettingsIntents.UpdateDotsOption -> {
                    changeDotsOption(intent.dots)
                }

                is SettingsIntents.UpdateTemperatureUnit -> {
                    changeTemperatureUnit(intent.unitTemp)
                }

                is SettingsIntents.UpdateWindSpeedUnit -> {
                    changeWindSpeedUnit(intent.unitWind)
                }

                is SettingsIntents.UpdatePrecipitationUnit -> {
                    changePrecipitationUnit(intent.unitPrecipitation)
                }

                is SettingsIntents.ApplySettings -> {
                    applySettings()
                }
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

    private fun changeDotsOption(dotsOption: Int) {
        _uiState.value = _uiState.value.copy(selectedDotsOption = dotsOption)
    }

    private suspend fun applySettings() {
        updateLanguageUseCase(_uiState.value.selectedLanguage)
        updateDotsOptionUseCase(_uiState.value.selectedDotsOption)
        updateTemperatureUnitUseCase(_uiState.value.selectedTemperatureUnit)
        updateWindSpeedUnitUseCase(_uiState.value.selectedWindSpeedUnit)
        updatePrecipitationUnitUseCase(_uiState.value.selectedPrecipitationUnit)
    }
}