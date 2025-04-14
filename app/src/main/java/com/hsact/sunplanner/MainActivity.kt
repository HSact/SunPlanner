package com.hsact.sunplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.hsact.sunplanner.ui.mainscreen.MainScreenUI
import com.hsact.sunplanner.ui.mainscreen.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val mainScreenUI = MainScreenUI(viewModel)
        @OptIn(ExperimentalMaterial3Api::class)
        setContent {
            SunPlannerTheme {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    //.fillMaxSize(),
                    //.systemBarsPadding()
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Sun Planner", style = MaterialTheme.typography.titleLarge)
                            },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { innerPadding ->
                    mainScreenUI.MainScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                        //.fillMaxSize()
                        //.padding(0.dp)
                        //.padding(top = innerPadding.calculateTopPadding())
                        //viewModel
                    )
                }
            }
        }
    }
}