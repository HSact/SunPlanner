package com.hsact.sunplanner.ui

import androidx.lifecycle.ViewModel
import com.hsact.sunplanner.ui.settings.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel() {
    private val _theme = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    val theme: StateFlow<ThemeMode> = _theme

    fun updateTheme(themeMode: ThemeMode) {
        _theme.value = themeMode
    }
}
