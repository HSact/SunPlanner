package com.hsact.sunplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.hsact.sunplanner.mainscreen.MainScreenUI
import com.hsact.sunplanner.mainscreen.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mainScreenUI = MainScreenUI()
        setContent {
            SunPlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    mainScreenUI.MainScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(0.dp),
                        viewModel
                    )
                }
            }
        }
    }
}