package com.hsact.sunplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsact.sunplanner.ui.theme.SunPlannerTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunPlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(10.dp)
                    )
                }
            }
        }
        val cityName = "Moscow"
        val startDate = "2024-01-01"
        val endDate = "2024-01-02"
        //viewModel.fetchWeatherByCity(cityName, startDate, endDate)
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var cityName by remember { mutableStateOf("") }
    var years by remember { mutableIntStateOf(1) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .weight(0.7f),
                value = cityName,
                onValueChange = { cityName = it },
                label = { Text("City") }
            )
            YearsPicker(years = years, onYearsChange = { years = it })
        }
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f),
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start date") }
            )
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("End date") }
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
        ) {
            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Search")
            }
        }
    }
}

@Composable
private fun RowScope.YearsPicker(years: Int, onYearsChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .weight(0.3f)
            .padding(start = 10.dp)

            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { if (years > 1) onYearsChange(years - 1) },
            enabled = years > 1
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
        }
        Text(text = years.toString(), fontSize = 18.sp)
        IconButton(
            onClick = { if (years < 10) onYearsChange(years + 1) },
            enabled = years < 10
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SunPlannerTheme {
        MainScreen()
    }
}