package com.hsact.sunplanner

import android.app.Application
import android.os.Build
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.nameToLanguageMode
import com.hsact.sunplanner.ui.utils.AppLocaleManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

/**
 * The custom [Application] class for the SunPlanner app.
 *
 * This class is annotated with [HiltAndroidApp] to enable dependency injection via Hilt.
 * It initializes Firebase and sets up application-wide configurations, such as language settings.
 */
@HiltAndroidApp
class SunPlanner : Application() {

    /**
     * Use case for retrieving the saved language setting from user preferences.
     * Injected via Hilt.
     */
    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    /**
     * Manager class responsible for changing the app's language at runtime.
     * Injected via Hilt.
     */
    @Inject
    lateinit var appLocaleManager: AppLocaleManager

    /**
     * Flag indicating whether the device is running an Android version older than Android 13 (Tiramisu).
     */
    val isPreAndroid13 = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU

    /**
     * Called when the application is starting, before any activity, service, or receiver objects have been created.
     *
     * Initializes Firebase, logs the app open event, and applies the previously saved language setting.
     */
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
        Log.d("FirebaseInit", "Firebase initialized: ${FirebaseApp.getInstance().name}")
        Log.d("FirebaseInit", "Firebase analytics: ${Firebase.analytics}")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
        applySavedLanguage()
    }

    /**
     * Applies the saved language from user settings.
     *
     * - On Android versions below 13, it uses a blocking call ([runBlocking]) since per-app locale
     *   APIs are not available.
     * - On Android 13 and above, it launches a coroutine to update the locale asynchronously.
     */
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

    /**
     * Sets the application locale and layout direction based on the selected [LanguageMode].
     *
     * @param languageMode The language to apply (e.g., English or Russian).
     */
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