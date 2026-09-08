package com.hsact.sunplanner.ui.utils

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import javax.inject.Inject

/**
 * Manager responsible for updating the application's locale (language).
 * Handles both modern (Android 13+) and legacy (AppCompat) locale APIs.
 */
class AppLocaleManager @Inject constructor() {
    /**
     * Changes the application's language.
     *
     * @param context The context used to access system services.
     * @param languageCode The ISO 639-1 language code (e.g., "en", "ru").
     */
    fun changeLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }
}