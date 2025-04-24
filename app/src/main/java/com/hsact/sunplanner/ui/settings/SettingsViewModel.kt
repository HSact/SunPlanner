package com.hsact.sunplanner.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {
    fun handleIntent(intent: SettingsIntents) {
        when (intent) {
            is SettingsIntents.UpdateTheme -> {changeTheme(intent.theme)}
            is SettingsIntents.UpdateLanguage -> {changeLanguage(intent.language)}
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