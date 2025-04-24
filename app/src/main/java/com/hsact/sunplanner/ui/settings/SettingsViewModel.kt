package com.hsact.sunplanner.ui.settings

import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    fun handleIntent(intent: SettingsIntents) {
        when (intent) {
            is SettingsIntents.UpdateTheme -> {changeTheme(theme = intent.theme)}
            is SettingsIntents.UpdateLanguage -> {changeLanguage(language = intent.language)}
            is SettingsIntents.ApplySettings -> {applySettings()}
        }
    }
    private fun changeTheme(theme: String) {
    }
    private fun changeLanguage(language: String) {
    }
    private fun applySettings() {
        TODO("Not yet implemented")
    }
}