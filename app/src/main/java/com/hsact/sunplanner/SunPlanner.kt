package com.hsact.sunplanner

import android.app.Application
import android.os.Build
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
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

    val isPreAndroid13 = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU

    override fun onCreate() {
        super.onCreate()
        applySavedLanguage()
        val analytics = Firebase.analytics
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

    private fun applySavedLanguage() {
        if (isPreAndroid13) {
            runBlocking {
                val language = getSettingsUseCase.language.firstOrNull()
                language?.let {
                    setLocale(it)
                }
            }
        } else {
            CoroutineScope(Dispatchers.Default).launch {
                val langMode = getSettingsUseCase.language.firstOrNull()
                    ?: nameToLanguageMode(Locale.getDefault().language)
                appLocaleManager.changeLanguage(this@SunPlanner, langMode.toName())
                setLocale(langMode)
            }
        }
    }

    private fun setLocale(languageMode: LanguageMode) {
        @Suppress("DEPRECATION") val locale = when (languageMode) {
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