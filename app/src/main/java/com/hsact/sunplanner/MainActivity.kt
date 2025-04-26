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
import com.hsact.sunplanner.ui.ThemeViewModel
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.hsact.sunplanner.ui.mainscreen.MainScreenUI
import com.hsact.sunplanner.ui.mainscreen.MainViewModel
import com.hsact.sunplanner.ui.settings.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
        installSplashScreen()
        val mainScreenUI = MainScreenUI(viewModel)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeModeState = themeViewModel.theme.collectAsState()
            val themeMode = themeModeState.value
            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            /*var currentTheme by remember { mutableStateOf(isDarkTheme) }
            LaunchedEffect(isDarkTheme) {
                currentTheme = isDarkTheme
            }*/

            val onApplyTheme: (ThemeMode) -> Unit = { selectedTheme ->
                themeViewModel.updateTheme(selectedTheme) }
            SunPlannerTheme (darkTheme = isDarkTheme) {
                mainScreenUI.MainScreen(onApplyTheme = onApplyTheme)
            }
        }
    }
}