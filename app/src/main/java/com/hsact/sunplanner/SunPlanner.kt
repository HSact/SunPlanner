package com.hsact.sunplanner

import android.app.Application
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class SunPlanner : Application() {

    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    override fun onCreate() {
        super.onCreate()

        applySavedLanguage()
    }

    private fun applySavedLanguage() {
        runBlocking {
            val language = getSettingsUseCase.language.firstOrNull()
            language?.let {
                setLocale(it)
            }
        }
    }

    private fun setLocale(languageMode: LanguageMode) {
        val locale = when (languageMode) {
            LanguageMode.ENGLISH -> Locale.ENGLISH
            LanguageMode.RUSSIAN -> Locale("ru")
        }

        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}