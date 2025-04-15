package com.hsact.sunplanner.ui.mainscreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.hsact.sunplanner.data.utils.LocationUtils
import com.hsact.sunplanner.ui.mainscreen.cards.WeatherGraphLineCard
import com.hsact.sunplanner.ui.mainscreen.searchUiKit.DropDownPicker
import com.hsact.sunplanner.ui.mainscreen.searchUiKit.SearchUI
import java.time.LocalDate

class MainScreenUI(val viewModel: MainViewModel) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        var cityName by remember { mutableStateOf("") }
        var isSearchExpanded by remember { mutableStateOf(false) }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val scrollState = rememberScrollState()
        val canScroll = remember { mutableStateOf(false) }
        val searchDataUI by viewModel.searchDataUI.collectAsState()
        val searchUI = SearchUI()
        var query by remember { mutableStateOf("") }

        val date1 = searchDataUI.startLD
        var years1 by remember {
            mutableStateOf(
                (1940..LocalDate.now().minusYears(1).year).toList().reversed()
            )
        }
        val months1 by remember { mutableStateOf((1..12).toList()) }
        val days1 = remember (date1) {(1..date1.lengthOfMonth()).toList()}

        val date2 = searchDataUI.endLD
        val years2 by remember {
            mutableStateOf(
                (1940..LocalDate.now().minusYears(1).year).toList().reversed()
            )
        }
        val months2 by remember { mutableStateOf((1..12).toList()) }
        val days2 = remember (date2) {(1..date2.lengthOfMonth()).toList()}

        //viewModel.fetchWeatherByCity("Moscow", "01.01.2024", "02.01.2024")
        //viewModel.fetchCityList(cityName)
        val context = LocalContext.current

        LaunchedEffect(scrollState.maxValue) {
            canScroll.value = scrollState.maxValue > 0
        }
        LaunchedEffect(searchDataUI.error) {
            if (searchDataUI.error.isNotEmpty()) {
                Toast.makeText(context, searchDataUI.error, Toast.LENGTH_SHORT).show()
                viewModel.cleanError()
            }
        }

        Scaffold(
            modifier = Modifier
                .then(if (canScroll.value) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier)
                .fillMaxSize(),
            topBar = {
                if (!isSearchExpanded) {
                    TopAppBar(
                        title = {
                            Text("Sun Planner", style = MaterialTheme.typography.titleLarge)
                        },
                        scrollBehavior = if (canScroll.value) scrollBehavior else null
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        top = if (isSearchExpanded) 0.dp else innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier
                        .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                ) {
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
                if (!isSearchExpanded) {
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
                                viewModel.updateStartYear(it)
                            },
                            modifier = Modifier.weight(0.5f)
                        )
                        DropDownPicker().ItemsDropdown(
                            label = "End year",
                            list = years2,
                            selected = date2.year,
                            onSelected = {
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
                                        viewModel.updateStartMonth(it)
                                    },
                                    modifier = Modifier.weight(0.5f)
                                )
                                DropDownPicker().ItemsDropdown(
                                    label = "End month",
                                    list = months2,
                                    selected = date2.monthValue,
                                    onSelected = {
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
                                        viewModel.updateStartDay(it)
                                    },
                                    modifier = Modifier.weight(0.5f)
                                )
                                DropDownPicker().ItemsDropdown(
                                    label = "End day",
                                    list = days2,
                                    selected = date2.dayOfMonth,
                                    onSelected = {
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
                        //Text("Weather: ${searchDataUI.weatherData}")
                        if (searchDataUI.maxTemperature != null) {
                            WeatherGraphLineCard().WeatherCard(
                                "Temperature",
                                listOf(searchDataUI.maxTemperature!!, searchDataUI.minTemperature!!)
                            )
                        }
                    }
                    Row(modifier.fillMaxWidth())
                    {
                        //Text("Weather: ${searchDataUI.weatherData}")
                        if (searchDataUI.sunDuration != null) {
                            WeatherGraphLineCard().WeatherCard(
                                "Sunshine hours",
                                listOf(searchDataUI.sunDuration!!)
                            )
                        }
                    }
                    /*if (searchDataUI.maxTemperature != null) {
                        WeatherGraphLineCard().WeatherCard(
                            "Temperature",
                            listOf(searchDataUI.maxTemperature!!, searchDataUI.minTemperature!!)
                        )
                    }*/
                }
            }
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