package com.hsact.sunplanner.ui.mainscreen

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import com.hsact.sunplanner.ui.components.cards.WeatherGraphLineCard
import java.time.LocalDate
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hsact.sunplanner.domain.model.DatesBundle
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.WeatherGraphData
import com.hsact.sunplanner.ui.components.CollapsibleTopBar
import com.hsact.sunplanner.ui.components.DropdownPicker
import com.hsact.sunplanner.ui.components.cards.WeatherGraphBarsCard
import com.hsact.sunplanner.ui.components.LocationSearch
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.SettingsDialog
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.toIndex
import kotlinx.coroutines.FlowPreview
import java.util.Locale
import kotlin.collections.toList

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@SuppressLint("LocalContextConfigurationRead")
@Composable
fun MainScreen(
    viewModel: MainViewModel,
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
    var query by remember {
        mutableStateOf(
            if (mainDataUI.settingsBundle.location != null)
                LocationUtils.buildCityFullName(mainDataUI.settingsBundle.location!!) else ""
        )
    }

    LaunchedEffect(scrollState.maxValue) {
        canScroll.value = scrollState.maxValue > 0 && !mainDataUI.isLoading
    }
    LaunchedEffect(mainDataUI.error) {
        if (mainDataUI.error.isNotEmpty()) {
            Toast.makeText(context, mainDataUI.error, Toast.LENGTH_SHORT).show()
            viewModel.handleIntent(MainScreenIntents.CleanError)
        }
    }
    LaunchedEffect(mainDataUI.settingsBundle.location) {
        if (query.isBlank() && mainDataUI.settingsBundle.location != null) {
            query =
                LocationUtils.buildCityFullName(
                    mainDataUI.settingsBundle.location ?: return@LaunchedEffect
                )
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
            SettingsDialog(hiltViewModel(), onApplyTheme, onChangeLanguage) {
                showSettingsDialog = false
            }
        }
        val topPadding = if (isSearchExpanded) 0.dp else innerPadding.calculateTopPadding()
        val bottomPadding = innerPadding.calculateBottomPadding()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = bottomPadding)
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
                LocationSearch(
                    viewModel = viewModel,
                    query = query,
                    onQueryChange = { query = it },
                    onCitySelected = { selectedCity ->
                        viewModel.handleIntent(MainScreenIntents.UpdateLocation(selectedCity))
                        isSearchExpanded = false
                        cityName = LocationUtils.buildCityFullName(selectedCity)
                        query = cityName
                    },
                    isSearchExpanded = isSearchExpanded,
                    onSearchExpandedChange = { isSearchExpanded = it }
                )
            }
            if (!isSearchExpanded) {
                YearsRangeSelection(viewModel, mainDataUI.tempDates)
                DatesRangeSection(viewModel, context, mainDataUI.tempDates)
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = { viewModel.handleIntent(MainScreenIntents.WeatherSearchClick) },
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth(),
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
                    DateText(modifier, mainDataUI)
                    WeatherCards(
                        context,
                        modifier,
                        mainDataUI.confirmedDates,
                        mainDataUI.weatherGraphData,
                        mainDataUI.settingsBundle
                    )
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

@OptIn(FlowPreview::class)
@Composable
private fun YearsRangeSelection(
    viewModel: MainViewModel,
    dates: DatesBundle
) {
    val yearChoices by remember {
        mutableStateOf(
            (1940..LocalDate.now().minusYears(1).year).toList().reversed()
        )
    }
    Row(
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
    ) {
        DropdownPicker(
            label = stringResource(R.string.start_year),
            list = yearChoices,
            selected = dates.startDate.year,
            onSelected = {
                viewModel.handleIntent(MainScreenIntents.UpdateStartYear(it))
            },
            modifier = Modifier
                .weight(0.5f)
                .padding(end = 3.dp)
        )
        DropdownPicker(
            label = stringResource(R.string.end_year),
            list = yearChoices,
            selected = dates.endDate.year,
            onSelected = {
                viewModel.handleIntent(MainScreenIntents.UpdateEndYear(it))
            },
            modifier = Modifier
                .weight(0.5f)
                .padding(start = 3.dp)
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun ColumnScope.DatesRangeSection(
    viewModel: MainViewModel,
    context: Context,
    datesBundle: DatesBundle,
) {
    val date1 = datesBundle.startDate
    val date2 = datesBundle.endDate
    val monthChoices = remember { context.resources.getStringArray(R.array.month_choices).toList() }
    val startDayChoices = remember(date1) { (1..date1.lengthOfMonth()).toList() }
    val endDayChoices = remember(date2) { (1..date2.lengthOfMonth()).toList() }
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
                        DropdownPicker(
                            label = stringResource(R.string.start_month),
                            list = monthChoices,
                            selected = monthChoices[date1.monthValue - 1],
                            onSelected = {
                                viewModel.handleIntent(
                                    MainScreenIntents.UpdateStartMonth(
                                        monthChoices.indexOf(it) + 1
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(end = 3.dp)
                        )
                        DropdownPicker(
                            label = stringResource(R.string.end_month),
                            list = monthChoices,
                            selected = monthChoices[date2.monthValue - 1],
                            onSelected = {
                                viewModel.handleIntent(
                                    MainScreenIntents.UpdateEndMonth(
                                        monthChoices.indexOf(
                                            it
                                        ) + 1
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(start = 3.dp)
                        )
                    }
                    Row(modifier = Modifier.padding(10.dp))
                    {
                        DropdownPicker(
                            label = stringResource(R.string.start_day),
                            list = startDayChoices,
                            selected = date1.dayOfMonth,
                            onSelected = {
                                viewModel.handleIntent(MainScreenIntents.UpdateStartDay(it))
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(end = 3.dp)
                        )
                        DropdownPicker(
                            label = stringResource(R.string.end_day),
                            list = endDayChoices,
                            selected = date2.dayOfMonth,
                            onSelected = {
                                viewModel.handleIntent(MainScreenIntents.UpdateEndDay(it))
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

@Composable
private fun DateText(
    modifier: Modifier,
    mainDataUI: MainUIState
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = DateUtils.formatDateRange(
                startDate = mainDataUI.confirmedDates.startDate,
                endDate = mainDataUI.confirmedDates.endDate,
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
}

@Composable
private fun WeatherCards(
    context: Context,
    modifier: Modifier,
    dates: DatesBundle,
    weatherGraphData: WeatherGraphData,
    settingsBundle: SettingsBundle
) {
    val tempUnit = context.resources.getStringArray(R.array.temp_unit_choices)
        .toList()[settingsBundle.temperatureUnitMode.toIndex()]
    val speedUnit = context.resources.getStringArray(R.array.speed_unit_choices)
        .toList()[settingsBundle.windUnitMode.toIndex()]
    val precipitationUnit = context.resources.getStringArray(R.array.precipitation_unit_choices)
        .toList()[settingsBundle.precipitationUnitMode.toIndex()]

    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        //Text("Weather: ${searchDataUI.weatherData}")
        WeatherGraphLineCard(
            stringResource(R.string.temperature)
                    + " (" + tempUnit + ")",
            listOf(
                weatherGraphData.maxTemperature!!,
                weatherGraphData.avgTemperature!!,
                weatherGraphData.minTemperature!!
            ),
            dates.startDate,
            dates.endDate,
            settingsBundle.languageMode.toLocale(),
            settingsBundle.themeMode
        )
    }
    Row(modifier.fillMaxWidth())
    {
        WeatherGraphLineCard(
            stringResource(R.string.sun_hours),
            listOf(
                weatherGraphData.dayLightDuration!!,
                weatherGraphData.sunShineDuration!!,
            ),
            dates.startDate,
            dates.endDate,
            settingsBundle.languageMode.toLocale(),
            settingsBundle.themeMode,
            true              //set min value 0
        )
    }
    Row(modifier.fillMaxWidth())
    {
        WeatherGraphBarsCard(
            stringResource(R.string.precipitation)
                    + " (" + precipitationUnit + ")",
            listOf(weatherGraphData.precipitation!!),
            DateUtils.generatePopUpLabels(
                dates.startDate,
                dates.endDate,
                settingsBundle.languageMode.toLocale()
            ),
            dates.startDate,
            dates.endDate,
            settingsBundle.languageMode.toLocale(),
            settingsBundle.themeMode
        )
    }
    Row(modifier.fillMaxWidth()) {
        WeatherGraphLineCard(
            stringResource(R.string.wind_speed)
                    + " (" + speedUnit + ")",
            listOf(
                weatherGraphData.windSpeed!!,
                weatherGraphData.windGustsSpeed!!
            ),
            dates.startDate,
            dates.endDate,
            settingsBundle.languageMode.toLocale(),
            settingsBundle.themeMode,
            true
        )
    }
}

/*@OptIn(FlowPreview::class)
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {

    SunPlannerTheme {
        MainScreen(
            viewModel = null,
            onApplyTheme = {},
            onChangeLanguage = {}
        )
    }
}*/