package com.hsact.sunplanner.ui.settings

sealed class SettingsIntents {
    data class UpdateTheme(val theme: String) : SettingsIntents()
    data class UpdateLanguage(val language: String) : SettingsIntents()
}