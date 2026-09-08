package com.hsact.sunplanner.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A lightweight ViewModel used to manage the app-wide theme state.
 * This is primarily used in MainActivity to observe theme changes and update the UI.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {
    private val _theme = MutableStateFlow(ThemeMode.SYSTEM)

    /**
     * Observable flow of the current [ThemeMode].
     */
    val theme: StateFlow<ThemeMode> = _theme

    init {
        viewModelScope.launch {
            getSettingsUseCase.theme.collect { theme: ThemeMode ->
                _theme.value = theme
            }
        }
    }

    /**
     * Manually updates the theme state.
     *
     * @param themeMode The new [ThemeMode] to apply.
     */
    fun updateTheme(themeMode: ThemeMode) {
        _theme.value = themeMode
    }
}