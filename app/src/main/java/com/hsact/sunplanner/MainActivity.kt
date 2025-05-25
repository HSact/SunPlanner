package com.hsact.sunplanner

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.ui.AppLocaleManager
import com.hsact.sunplanner.ui.ThemeViewModel
import com.hsact.sunplanner.ui.components.cards.WeatherGraphDataFactory
import com.hsact.sunplanner.ui.mainscreen.MainScreen
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.hsact.sunplanner.ui.mainscreen.MainViewModel
import com.hsact.sunplanner.ui.settings.LocalizedContextWrapper
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@OptIn(FlowPreview::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    @Inject
    lateinit var appLocaleManager: AppLocaleManager

    @Inject
    lateinit var weatherGraphDataFactory: WeatherGraphDataFactory

    private lateinit var selectedLocale: Locale
    val isPreAndroid13 = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU

    override fun attachBaseContext(base: Context) {
        if (isPreAndroid13) {
            selectedLocale = Locale.getDefault()
            val localizedContext = LocalizedContextWrapper.wrap(base, selectedLocale)
            super.attachBaseContext(localizedContext)
        } else
            super.attachBaseContext(base)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            getSettingsUseCase.language.firstOrNull()?.let { languageMode ->
                selectedLocale = when (languageMode) {
                    LanguageMode.ENGLISH -> Locale.ENGLISH
                    LanguageMode.RUSSIAN -> Locale("ru")
                }
                setAppLocale(languageMode)
            }
        }

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
        installSplashScreen()
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeModeState = themeViewModel.theme.collectAsState()
            val themeMode = themeModeState.value
            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            if (isPreAndroid13) {
                ApplyThemeLegacy(isDarkTheme)
            }
            val onApplyTheme: (ThemeMode) -> Unit = { selectedTheme ->
                themeViewModel.updateTheme(selectedTheme)
            }
            val onChangeLanguage: (LanguageMode) -> Unit = { selectedLanguage ->
                setAppLocale(selectedLanguage)
                if (isPreAndroid13) {
                    setAppLocaleLegacy(selectedLanguage)
                }
            }
            SunPlannerTheme(darkTheme = isDarkTheme) {
                MainScreen(
                    viewModel,
                    weatherGraphDataFactory = weatherGraphDataFactory,
                    onApplyTheme = onApplyTheme,
                    onChangeLanguage = onChangeLanguage
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    @Composable
    private fun ApplyThemeLegacy(isDarkTheme: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDarkTheme
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        window.navigationBarColor = if (isDarkTheme) {
            0xFF000000.toInt()
        } else {
            0xFFFFFFFF.toInt()
        }
        controller.isAppearanceLightStatusBars = !isDarkTheme
        controller.isAppearanceLightNavigationBars = !isDarkTheme
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
    }

    private fun setAppLocale(languageMode: LanguageMode) {
        appLocaleManager.changeLanguage(this, languageMode.toName())
    }

    @Suppress("DEPRECATION")
    private fun setAppLocaleLegacy(languageMode: LanguageMode) {
        val locale = when (languageMode) {
            LanguageMode.ENGLISH -> Locale.ENGLISH
            LanguageMode.RUSSIAN -> Locale("ru")
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
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