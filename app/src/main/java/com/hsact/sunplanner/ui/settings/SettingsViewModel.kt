package com.hsact.sunplanner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.PrecipitationUnitMode
import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode
import com.hsact.sunplanner.domain.repository.BookmarkRepository
import com.hsact.sunplanner.domain.repository.WeatherRepository
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateCurveOptionUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateDotsOptionUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLanguageUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdatePrecipitationUnitUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateTemperatureUnitUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateThemeUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateWindSpeedUnitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Settings screen, responsible for managing user preferences
 * and app data (cache).
 *
 * Uses domain-level use cases to interact with settings storage and repositories.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateDotsOptionUseCase: UpdateDotsOptionUseCase,
    private val updateCurveOptionUseCase: UpdateCurveOptionUseCase,
    private val updateTemperatureUnitUseCase: UpdateTemperatureUnitUseCase,
    private val updateWindSpeedUnitUseCase: UpdateWindSpeedUnitUseCase,
    private val updatePrecipitationUnitUseCase: UpdatePrecipitationUnitUseCase,
    private val weatherRepository: WeatherRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUIState())
    /**
     * Observable state for the Settings UI.
     */
    val uiState: StateFlow<SettingsUIState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    /**
     * Sets up a combined observer for all setting preferences.
     * Updates [uiState] whenever any preference changes.
     */
    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                getSettingsUseCase.theme,
                getSettingsUseCase.language,
                getSettingsUseCase.temperatureUnit,
                getSettingsUseCase.windUnit,
                getSettingsUseCase.precipitationUnit,
                getSettingsUseCase.isDotsVisible,
                getSettingsUseCase.isEdgesCurved
            ) { args: Array<Any?> ->
                val theme = args[0] as ThemeMode
                val language = args[1] as? LanguageMode
                val tempUnit = args[2] as TemperatureUnitMode
                val windUnit = args[3] as WindSpeedUnitMode
                val precUnit = args[4] as PrecipitationUnitMode
                val showDots = args[5] as Boolean
                val isCurved = args[6] as Boolean

                SettingsUIState(
                    theme = theme,
                    language = language ?: LanguageMode.fromName(Locale.getDefault().language),
                    temperatureUnit = tempUnit,
                    windSpeedUnit = windUnit,
                    precipitationUnit = precUnit,
                    isDotsVisible = showDots,
                    isEdgesCurved = isCurved
                )
            }.collect { newState ->
                _uiState.update {
                    newState.copy(
                        isClearCacheDialogOpen = it.isClearCacheDialogOpen,
                        appVersion = it.appVersion
                    )
                }
            }
        }
    }

    /**
     * Processes user intents from the Settings screen.
     *
     * @param intent The [SettingsIntents] to handle.
     */
    fun handleIntent(intent: SettingsIntents) {
        viewModelScope.launch {
            when (intent) {
                is SettingsIntents.UpdateTheme -> updateThemeUseCase(intent.theme)
                is SettingsIntents.UpdateLanguage -> updateLanguageUseCase(intent.language)
                is SettingsIntents.UpdateDotsOption -> updateDotsOptionUseCase(if (intent.isVisible) 1 else 0)
                is SettingsIntents.UpdateCurveOption -> updateCurveOptionUseCase(if (intent.isCurved) 1 else 0)
                is SettingsIntents.UpdateTemperatureUnit -> updateTemperatureUnitUseCase(intent.unitTemp)
                is SettingsIntents.UpdateWindSpeedUnit -> updateWindSpeedUnitUseCase(intent.unitWind)
                is SettingsIntents.UpdatePrecipitationUnit -> updatePrecipitationUnitUseCase(intent.unitPrecipitation)
                is SettingsIntents.SetClearCacheDialogVisible -> _uiState.update {
                    it.copy(
                        isClearCacheDialogOpen = intent.visible
                    )
                }

                is SettingsIntents.ClearCache -> {
                    weatherRepository.clearCache()
                    bookmarkRepository.clearAll()
                    _uiState.update { it.copy(isClearCacheDialogOpen = false) }
                }
            }
        }
    }

    /**
     * Updates the application version string in the UI state.
     *
     * @param version The version string (e.g., from BuildConfig).
     */
    fun setAppVersion(version: String) {
        _uiState.update { it.copy(appVersion = version) }
    }
}
