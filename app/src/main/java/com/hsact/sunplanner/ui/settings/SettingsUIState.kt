package com.hsact.sunplanner.ui.settings

data class SettingsUIState(
    var currentTheme: ThemeMode = ThemeMode.SYSTEM,
    var currentLanguage: LanguageMode = LanguageMode.ENGLISH,
    var selectedTheme: ThemeMode = currentTheme,
    var selectedLanguage: LanguageMode = currentLanguage,
)