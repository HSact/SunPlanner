package com.hsact.sunplanner.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.hsact.sunplanner.data.LocationUtils
import com.hsact.sunplanner.mainscreen.searchUiKit.SearchUI

class MainScreenUI(val viewModel: MainViewModel) {

    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        var cityName by remember { mutableStateOf("") }
        var years by remember { mutableIntStateOf(1) }
        var startYear by remember { mutableStateOf("") }
        var endYear by remember { mutableStateOf("") }
        var startDate by remember { mutableStateOf("") }
        var endDate by remember { mutableStateOf("") }
        var isSearchExpanded by remember { mutableStateOf(false) }
        val searchDataUI by viewModel.searchDataUI.collectAsState()
        val searchUI = SearchUI()
        var query by remember { mutableStateOf("") }

        //viewModel.fetchWeatherByCity(cityName, startDate, endDate)
        //viewModel.fetchCityList(cityName)

        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Row {
                searchUI.SearchCityBar(
                    viewModel = viewModel,
                    query = query,
                    onQueryChange = { query = it },
                    onCitySelected = { selectedCity ->
                        viewModel.saveLocationToVM(selectedCity)
                        isSearchExpanded = false
                        cityName = LocationUtils.buildCityFullName(selectedCity)
                        query = cityName
                        //viewModel.fetchWeatherByCity(cityName, startDate, endDate)
                    },
                    isSearchExpanded = isSearchExpanded,
                    onSearchExpandedChange = { isSearchExpanded = it }
                ) //{selectedCity -> onCityCardClick(selectedCity , searchUI)}
            }
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f),
                    value = startYear,
                    onValueChange = { startYear = it },
                    label = { Text("Start year") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    value = endYear,
                    onValueChange = { endYear = it },
                    label = { Text("End year") }
                )
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
            //Cards with weather data
        }
    }

    /*@Composable
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
    }*/
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val viewModel = MainViewModel()
    SunPlannerTheme {
        MainScreenUI(viewModel = viewModel).MainScreen()
    }
}