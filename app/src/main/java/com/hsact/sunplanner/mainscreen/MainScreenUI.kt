package com.hsact.sunplanner.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
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
import com.hsact.sunplanner.mainscreen.searchUiKit.YearDropDownPicker
import java.time.LocalDate

class MainScreenUI(val viewModel: MainViewModel) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        var cityName by remember { mutableStateOf("") }
        var startYear by remember { mutableIntStateOf(0) }
        var endYear by remember { mutableIntStateOf(0) }
        var selectedStartYear by remember { mutableIntStateOf(startYear) }
        var selectedEndYear by remember { mutableIntStateOf(endYear) }
        var startDate by remember { mutableStateOf("") }
        var endDate by remember { mutableStateOf("") }
        var isSearchExpanded by remember { mutableStateOf(false) }
        val searchDataUI by viewModel.searchDataUI.collectAsState()
        val searchUI = SearchUI()
        var query by remember { mutableStateOf("") }

        //viewModel.fetchWeatherByCity("Moscow", "01.01.2024", "02.01.2024")
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
                    },
                    isSearchExpanded = isSearchExpanded,
                    onSearchExpandedChange = { isSearchExpanded = it }
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                YearDropDownPicker().YearDropdown(
                    label = "Start year",
                    selectedYear = selectedStartYear,
                    onYearSelected = { selectedStartYear = it },
                    modifier = Modifier.weight(0.5f)
                )
                YearDropDownPicker().YearDropdown(
                    label = "End year",
                    selectedYear = selectedEndYear,
                    onYearSelected = { selectedEndYear = it },
                    modifier = Modifier.weight(0.5f).padding(start = 10.dp)
                )

                /*OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    value = endYear,
                    onValueChange = { endYear = it },
                    label = { Text("End year") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Icon"
                        )
                    }
                )*/
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
                    onClick = { viewModel.prepareParamsForRequest() },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("Search")
                }
            }
            Row(modifier.fillMaxWidth())
            {
                Text("Weather: ${searchDataUI.weatherData.toString()}")
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