package com.hsact.sunplanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.ui.ThemeViewModel
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.hsact.sunplanner.ui.mainscreen.MainScreenUI
import com.hsact.sunplanner.ui.mainscreen.MainViewModel
import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.LocalizedContextWrapper
import com.hsact.sunplanner.ui.settings.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

val LocalAppLocale = staticCompositionLocalOf { Locale.getDefault() }
val LocalAppContext = staticCompositionLocalOf<Context> { error("No Context provided") }
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    private lateinit var selectedLocale: Locale
    private val localeState = mutableStateOf(Locale.getDefault())

    override fun attachBaseContext(base: Context) {
        selectedLocale = Locale.getDefault()
        val localizedContext = LocalizedContextWrapper.wrap(base, selectedLocale)
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            getSettingsUseCase.language.firstOrNull()?.let { languageMode ->
                selectedLocale = when (languageMode) {
                    LanguageMode.ENGLISH -> Locale.ENGLISH
                    LanguageMode.RUSSIAN -> Locale("ru")
                }
            }
        }
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
        installSplashScreen()
        setContent {
            LaunchedEffect(Unit) {
                getSettingsUseCase.language.firstOrNull()?.let { languageMode ->
                    localeState.value = when (languageMode) {
                        LanguageMode.ENGLISH -> Locale.ENGLISH
                        LanguageMode.RUSSIAN -> Locale("ru")
                    }
                }
            }
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeModeState = themeViewModel.theme.collectAsState()
            val themeMode = themeModeState.value
            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val onApplyTheme: (ThemeMode) -> Unit = { selectedTheme ->
                themeViewModel.updateTheme(selectedTheme) }
            val onChangeLanguage: (LanguageMode) -> Unit = { selectedLanguage ->
                setAppLocale(selectedLanguage)
            }
            val localizedContext = remember(localeState.value) {
                LocalizedContextWrapper.wrap(this, localeState.value)
            }
            CompositionLocalProvider(LocalAppLocale provides localeState.value,
                LocalAppContext provides localizedContext) {
                SunPlannerTheme(darkTheme = isDarkTheme) {
                    MainScreenUI(viewModel).MainScreen(onApplyTheme = onApplyTheme, onChangeLanguage = onChangeLanguage)
                }
            }
        }
    }
    private fun setAppLocale(languageMode: LanguageMode) {
        val locale = when (languageMode) {
            LanguageMode.ENGLISH -> Locale.ENGLISH
            LanguageMode.RUSSIAN -> Locale("ru")
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
        restartApp()
    }
    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finishAffinity()
    }
}