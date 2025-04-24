package com.hsact.sunplanner.ui.settings

data class SettingsUIState(
    var currentTheme: ThemeMode = ThemeMode.SYSTEM,
    var currentLanguage: String = "auto",
    var selectedTheme: ThemeMode = currentTheme,
    var selectedLanguage: String = "auto",
)
