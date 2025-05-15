package com.hsact.sunplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.ui.AppLocaleManager
import com.hsact.sunplanner.ui.ThemeViewModel
import com.hsact.sunplanner.ui.mainscreen.MainScreen
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.hsact.sunplanner.ui.mainscreen.MainViewModel
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

    private lateinit var selectedLocale: Locale

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

            val onApplyTheme: (ThemeMode) -> Unit = { selectedTheme ->
                themeViewModel.updateTheme(selectedTheme)
            }
            val onChangeLanguage: (LanguageMode) -> Unit = { selectedLanguage ->
                setAppLocale(selectedLanguage)
            }
            SunPlannerTheme(darkTheme = isDarkTheme) {
                MainScreen(
                    viewModel,
                    onApplyTheme = onApplyTheme,
                    onChangeLanguage = onChangeLanguage
                )
            }
        }
    }

    private fun setAppLocale(languageMode: LanguageMode) {
        appLocaleManager.changeLanguage(this, languageMode.toName())
    }
}