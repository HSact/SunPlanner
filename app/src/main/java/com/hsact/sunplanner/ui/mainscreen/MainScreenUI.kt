package com.hsact.sunplanner.ui.mainscreen

import android.annotation.SuppressLint
import com.hsact.sunplanner.R
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.data.utils.LocationUtils
import com.hsact.sunplanner.ui.mainscreen.cards.WeatherGraphBarsLineCard
import com.hsact.sunplanner.ui.mainscreen.cards.WeatherGraphLineCard
import com.hsact.sunplanner.ui.DropDownPicker
import com.hsact.sunplanner.ui.mainscreen.searchUiKit.LocationSearchUI
import java.time.LocalDate
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.SettingsDialog
import com.hsact.sunplanner.ui.settings.ThemeMode
import com.hsact.sunplanner.ui.settings.unitModes.toIndex
import kotlinx.coroutines.FlowPreview
import java.util.Locale
import kotlin.collections.toList

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
class MainScreenUI(val viewModel: MainViewModel) {
    @SuppressLint("LocalContextConfigurationRead")
    @Composable
    fun MainScreen(
        modifier: Modifier = Modifier,
        onApplyTheme: (ThemeMode) -> Unit,
        onChangeLanguage: (LanguageMode) -> Unit
    ) {
        val mainDataUI by viewModel.mainUiState.collectAsState()
        val context = LocalContext.current
        var showSettingsDialog by remember { mutableStateOf(false) }
        var cityName by remember { mutableStateOf("") }
        var isSearchExpanded by remember { mutableStateOf(false) }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val scrollState = rememberScrollState()
        val canScroll = remember { mutableStateOf(false) }
        val searchUI = LocationSearchUI()
        var query by remember { mutableStateOf("")  }
        val date1 = mainDataUI.startLD
        var years1 by remember {
            mutableStateOf(
                (1940..LocalDate.now().minusYears(1).year).toList().reversed()
            )
        }
        val months1 by remember { mutableStateOf((1..12).toList()) }
        val days1 = remember(date1) { (1..date1.lengthOfMonth()).toList() }
        val date2 = mainDataUI.endLD
        val years2 by remember {
            mutableStateOf(
                (1940..LocalDate.now().minusYears(1).year).toList().reversed()
            )
        }
        val months2 by remember { mutableStateOf((1..12).toList()) }
        val days2 = remember(date2) { (1..date2.lengthOfMonth()).toList() }

        LaunchedEffect(scrollState.maxValue) {
            canScroll.value = scrollState.maxValue > 0 && !mainDataUI.isLoading
        }
        LaunchedEffect(mainDataUI.error) {
            if (mainDataUI.error.isNotEmpty()) {
                Toast.makeText(context, mainDataUI.error, Toast.LENGTH_SHORT).show()
                viewModel.cleanError()
            }
        }
        LaunchedEffect(mainDataUI.location) {
            if (query.isBlank() && mainDataUI.location != null) {
                query = LocationUtils.buildCityFullName(mainDataUI.location?:return@LaunchedEffect)
            }
        }
        Scaffold(
            modifier = Modifier
                .then(if (canScroll.value) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier)
                .fillMaxSize(),
            topBar = {
                if (!isSearchExpanded) {
                    CollapsibleTopBar(
                        scrollBehavior = scrollBehavior,
                        onSettingsClick = { showSettingsDialog = true }
                    )
                }
            }
        ) { innerPadding ->
            if (showSettingsDialog) {
                SettingsDialog(hiltViewModel(), onApplyTheme, onChangeLanguage).ShowDialog {
                    showSettingsDialog = false
                }
            }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        top = if (isSearchExpanded) 0.dp else innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                    .verticalScroll(scrollState)
            ) {
                val windowInfo = LocalWindowInfo.current
                val density = LocalDensity.current
                val screenHeight = with(density) { windowInfo.containerSize.height.toDp() }
                Row(
                    modifier = if (isSearchExpanded) Modifier
                        .heightIn(max = screenHeight)
                    else Modifier
                        .heightIn(max = screenHeight)
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    searchUI.SearchCityBar(
                        viewModel = viewModel,
                        query = query,
                        onQueryChange = { query = it },
                        onCitySelected = { selectedCity ->
                            viewModel.updateLocation(selectedCity)
                            isSearchExpanded = false
                            cityName = LocationUtils.buildCityFullName(selectedCity)
                            query = cityName
                        },
                        isSearchExpanded = isSearchExpanded,
                        onSearchExpandedChange = { isSearchExpanded = it }
                    )
                }
                if (!isSearchExpanded) {
                    val dropDownPicker = DropDownPicker()
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .fillMaxWidth()
                    ) {
                        dropDownPicker.ItemsDropdown(
                            label = stringResource(R.string.start_year),
                            list = years1,
                            selected = date1.year,
                            onSelected = {
                                viewModel.updateStartYear(it)
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(end = 3.dp)
                        )
                        dropDownPicker.ItemsDropdown(
                            label = stringResource(R.string.end_year),
                            list = years2,
                            selected = date2.year,
                            onSelected = {
                                viewModel.updateEndYear(it)
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(start = 3.dp)
                        )
                    }
                    DatesRangeSection(date1, months1, days1, date2, months2, days2)
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.onWeatherSearchClick() },
                            modifier = Modifier
                                .weight(1f),
                            enabled = !mainDataUI.isLoading
                        ) {
                            Text(stringResource(R.string.search))
                        }
                    }
                    if (mainDataUI.isLoading) {
                        Row(
                            modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(64.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }
                    if (mainDataUI.weatherData != null && !mainDataUI.isLoading) {
                        val tempUnit = context.resources.getStringArray(R.array.temp_unit_choices)
                            .toList()[mainDataUI.temperatureUnitMode.toIndex()]
                        val speedUnit = context.resources.getStringArray(R.array.speed_unit_choices)
                            .toList()[mainDataUI.windUnitMode.toIndex()]
                        val precipitationUnit = context.resources.getStringArray(R.array.precipitation_unit_choices)
                            .toList()[mainDataUI.precipitationUnitMode.toIndex()]
                        Row(
                            modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = DateUtils.formatDateRange(
                                    startDate = mainDataUI.confirmedStartLD,
                                    endDate = mainDataUI.confirmedEndLD,
                                    isOneDay = mainDataUI.isOneDay,
                                    isOneYear = mainDataUI.isOneYear,
                                    locale = Locale.getDefault(),
                                    singleDayOneYearString = stringResource(R.string.single_day_one_year),
                                    singleDaySting = stringResource(R.string.single_day_range),
                                    dateRangeOneYearSting = stringResource(R.string.date_range_one_year),
                                    dateRangeString = stringResource(R.string.date_range)
                                ),
                            )
                        }

                        Row(
                            modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        ) {
                            //Text("Weather: ${searchDataUI.weatherData}")
                            WeatherGraphLineCard().WeatherCard(
                                stringResource(R.string.temperature)
                                        + " (" + tempUnit + ")",
                                listOf(
                                    mainDataUI.maxTemperature!!,
                                    mainDataUI.avgTemperature!!,
                                    mainDataUI.minTemperature!!
                                ),
                                mainDataUI.confirmedStartLD,
                                mainDataUI.confirmedEndLD,
                                mainDataUI.themeMode
                            )
                        }
                        Row(modifier.fillMaxWidth())
                        {
                            WeatherGraphLineCard().WeatherCard(
                                stringResource(R.string.sunshine_hours),
                                listOf(mainDataUI.sunDuration!!),
                                mainDataUI.confirmedStartLD,
                                mainDataUI.confirmedEndLD,
                                mainDataUI.themeMode,
                                true              //set min value 0
                            )
                        }
                        Row(modifier.fillMaxWidth())
                        {
                            WeatherGraphBarsLineCard().WeatherCard(
                                stringResource(R.string.precipitation)
                                        + " (" + precipitationUnit + ")",
                                listOf(mainDataUI.precipitation!!),
                                mainDataUI.confirmedStartLD,
                                mainDataUI.confirmedEndLD,
                                mainDataUI.themeMode
                            )
                        }
                        Row(modifier.fillMaxWidth()) {
                            WeatherGraphLineCard().WeatherCard(
                                stringResource(R.string.wind_speed)
                                        + " (" + speedUnit + ")",
                                listOf(
                                    mainDataUI.windSpeed!!,
                                    mainDataUI.windGustsSpeed!!
                                ),
                                mainDataUI.confirmedStartLD,
                                mainDataUI.confirmedEndLD,
                                mainDataUI.themeMode,
                                true
                            )
                        }
                        Row(
                            modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp)
                        ) {
                            Text(stringResource(R.string.data_source))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CollapsibleTopBar(
        scrollBehavior: TopAppBarScrollBehavior,
        onSettingsClick: () -> Unit
    ) {
        TopAppBar(
            title = {
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    private fun ColumnScope.DatesRangeSection(
        date1: LocalDate,
        months1: List<Int>,
        days1: List<Int>,
        date2: LocalDate,
        months2: List<Int>,
        days2: List<Int>
    ) {
        val dropDownPicker = DropDownPicker()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
        {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.dates_range),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 8.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 8.dp)
                        .zIndex(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(
                            top = 10.dp,
                            start = 5.dp,
                            end = 5.dp,
                            bottom = 5.dp
                        )
                        .align(Alignment.TopCenter)
                        .zIndex(0f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .align(Alignment.CenterHorizontally)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 8.dp)
                        ) {

                        }
                        Row(
                            modifier = Modifier
                                .padding(
                                    top = 10.dp, start = 10.dp,
                                    end = 10.dp, bottom = 10.dp
                                )
                        ) {
                            dropDownPicker.ItemsDropdown(
                                label = stringResource(R.string.start_month),
                                list = months1,
                                selected = date1.monthValue,
                                onSelected = {
                                    viewModel.updateStartMonth(it)
                                },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(end = 3.dp)
                            )
                            dropDownPicker.ItemsDropdown(
                                label = stringResource(R.string.end_month),
                                list = months2,
                                selected = date2.monthValue,
                                onSelected = {
                                    viewModel.updateEndMonth(it)
                                },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(start = 3.dp)
                            )
                        }
                        Row(modifier = Modifier.padding(10.dp))
                        {
                            dropDownPicker.ItemsDropdown(
                                label = stringResource(R.string.start_day),
                                list = days1,
                                selected = date1.dayOfMonth,
                                onSelected = {
                                    viewModel.updateStartDay(it)
                                },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(end = 3.dp)
                            )
                            dropDownPicker.ItemsDropdown(
                                label = stringResource(R.string.end_day),
                                list = days2,
                                selected = date2.dayOfMonth,
                                onSelected = {
                                    viewModel.updateEndDay(it)
                                },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(start = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val fakeUseCase = null
    val viewModel = MainViewModel(fakeUseCase!!)
    SunPlannerTheme {
        MainScreenUI(viewModel = viewModel).MainScreen()
    }
}*/