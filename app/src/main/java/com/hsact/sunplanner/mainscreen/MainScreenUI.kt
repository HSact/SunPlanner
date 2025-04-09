package com.hsact.sunplanner.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.hsact.sunplanner.mainscreen.searchUiKit.DropDownPicker
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MainScreenUI(val viewModel: MainViewModel) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        //val years = (1940 until LocalDate.now().year).toList().reversed()
        /*val months: List<String> = List(12) {
            LocalDate.of(0, it + 1, 1).month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        }*/
        /*val startDays = (1..28).toList()
        val endDays = (1..28).toList()*/
        var cityName by remember { mutableStateOf("") }
        /*var startYear by remember { mutableIntStateOf(LocalDate.now().year - 10) }
        var endYear by remember { mutableIntStateOf(LocalDate.now().year - 1) }
        var startMonth by remember { mutableStateOf("") }
        var endMonth by remember { mutableStateOf("") }
        var startDay by remember { mutableIntStateOf(0) }
        var endDay by remember { mutableIntStateOf(0) }
        var selectedStartYear by remember { mutableIntStateOf(startYear) }
        var selectedEndYear by remember { mutableIntStateOf(endYear) }
        var selectedStartMonth by remember { mutableStateOf(startMonth) }
        var selectedEndMonth by remember { mutableStateOf(endMonth) }
        var selectedStartDay by remember { mutableIntStateOf(startDay) }
        var selectedEndDay by remember { mutableIntStateOf(endDay) }*/
        var isSearchExpanded by remember { mutableStateOf(false) }
        val searchDataUI by viewModel.searchDataUI.collectAsState()
        val searchUI = SearchUI()
        var query by remember { mutableStateOf("") }

        val date1 = LocalDate.now().minusYears(10)
        val years1 = (date1.year - 80..date1.year).toList().reversed()
        val months1 = (1..12).toList()
        val days1 = (1..date1.lengthOfMonth()).toList()

        val date2 = LocalDate.now().minusYears(1)
        val years2 = (date2.year - 80..date2.year).toList().reversed()
        val months2 = (1..12).toList()
        val days2 = (1..date1.lengthOfMonth()).toList()

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
                DropDownPicker().ItemsDropdown(
                    label = "Start year",
                    list = years1,
                    selected = date1.year,
                    onSelected = {
                        //date1.year = it
                        //viewModel.saveDateFieldToVM(field = DateField.START_YEAR, it)},
                        viewModel.updateStartYear(it)
                    },
                    modifier = Modifier.weight(0.5f)
                )
                DropDownPicker().ItemsDropdown(
                    label = "End year",
                    list = years2,
                    selected = date2.year,
                    onSelected = {
                        //selectedEndYear = it
                        //viewModel.saveDateFieldToVM(field = DateField.END_YEAR, it)
                        viewModel.updateEndYear(it)
                    },
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = 5.dp)
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 20.dp, start = 5.dp, end = 5.dp)
            ) {
                Card()
                {
                    Row(
                        modifier = Modifier
                            .padding(
                                top = 10.dp, start = 5.dp,
                                end = 5.dp, bottom = 10.dp
                            )
                    ) {
                        DropDownPicker().ItemsDropdown(
                            label = "Start month",
                            list = months1,
                            selected = date1.monthValue,
                            onSelected = {
                                //selectedStartMonth = it
                                //viewModel.saveDateFieldToVM(field = DateField.START_MONTH, it)
                                viewModel.updateStartMonth(it)
                            },
                            modifier = Modifier.weight(0.5f)
                        )
                        DropDownPicker().ItemsDropdown(
                            label = "End month",
                            list = months2,
                            selected = date2.monthValue,
                            onSelected = {
                                //selectedEndMonth = it
                                //viewModel.saveDateFieldToVM(field = DateField.END_MONTH, it)
                                viewModel.updateEndMonth(it)
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(start = 5.dp)
                        )
                    }
                    Row(modifier = Modifier.padding(5.dp))
                    {
                        DropDownPicker().ItemsDropdown(
                            label = "Start day",
                            list = days1,
                            selected = date1.dayOfMonth,
                            onSelected = {
                                //selectedStartDay = it
                                //viewModel.saveDateFieldToVM(field = DateField.START_DAY, it)
                                viewModel.updateStartDay(it)
                            },
                            modifier = Modifier.weight(0.5f)
                        )
                        DropDownPicker().ItemsDropdown(
                            label = "End day",
                            list = days2,
                            selected = date2.dayOfMonth,
                            onSelected = {
                                //selectedEndDay = it
                                //viewModel.saveDateFieldToVM(field = DateField.END_DAY, it)
                                viewModel.updateEndDay(it)
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(start = 5.dp)
                        )
                    }
                }
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
                Text("Weather: ${searchDataUI.weatherData}")
            }
            //Cards with weather data
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val viewModel = MainViewModel()
    SunPlannerTheme {
        MainScreenUI(viewModel = viewModel).MainScreen()
    }
}