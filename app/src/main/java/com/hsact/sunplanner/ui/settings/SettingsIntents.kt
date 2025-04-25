package com.hsact.sunplanner.ui.settings

sealed class SettingsIntents {
    data class UpdateTheme(val theme: ThemeMode) : SettingsIntents()
    data class UpdateLanguage(val language: LanguageMode) : SettingsIntents()
    object ApplySettings : SettingsIntents()
}