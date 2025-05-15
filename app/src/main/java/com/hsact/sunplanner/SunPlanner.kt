package com.hsact.sunplanner

import android.app.Application
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.ui.AppLocaleManager
import com.hsact.sunplanner.ui.settings.modes.nameToLanguageMode
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
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
        CoroutineScope(Dispatchers.Default).launch {
            val langMode = getSettingsUseCase.language.firstOrNull()
                ?: nameToLanguageMode(Locale.getDefault().language)
            appLocaleManager.changeLanguage(this@SunPlanner, langMode.toName())
        }
    }
}