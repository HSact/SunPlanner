package com.hsact.sunplanner

import android.app.Application
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.ui.AppLocaleManager
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.nameToLanguageMode
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class SunPlanner : Application() {

    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    @Inject
    lateinit var appLocaleManager: AppLocaleManager

    override fun onCreate() {
        super.onCreate()
        applySavedLanguage()
    }

    private fun applySavedLanguage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            CoroutineScope(Dispatchers.Default).launch {
                val langMode = getSettingsUseCase.language.firstOrNull()
                    ?: nameToLanguageMode(Locale.getDefault().language)
                appLocaleManager.changeLanguage(this@SunPlanner, langMode.toName())
                setLocale(langMode)
            }
        } else {
            runBlocking {
                val language = getSettingsUseCase.language.firstOrNull()
                language?.let {
                    setLocale(it)
                }
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
        config.setLayoutDirection(locale)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}