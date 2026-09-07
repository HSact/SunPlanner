package com.hsact.sunplanner.ui.mainscreen

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hsact.sunplanner.R
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.data.utils.LocationUtils
import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.model.Bookmark
import com.hsact.sunplanner.domain.model.DatesBundle
import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WeatherMetricType
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.ui.components.CollapsibleTopBar
import com.hsact.sunplanner.ui.components.DropdownPicker
import com.hsact.sunplanner.ui.components.LocationSearch
import com.hsact.sunplanner.ui.components.cards.WeatherGraphBarsCard
import com.hsact.sunplanner.ui.components.cards.WeatherGraphDataFactory
import com.hsact.sunplanner.ui.components.cards.WeatherGraphLabels
import com.hsact.sunplanner.ui.components.cards.WeatherGraphLineCard
import com.hsact.sunplanner.ui.settings.SettingsDialog
import com.hsact.sunplanner.ui.theme.LocalExtendedColors
import com.hsact.sunplanner.ui.utils.stringArrayResource
import kotlinx.coroutines.FlowPreview
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class, ExperimentalSharedTransitionApi::class)
@SuppressLint("LocalContextConfigurationRead")
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    weatherGraphDataFactory: WeatherGraphDataFactory,
    onApplyTheme: (ThemeMode) -> Unit,
    onChangeLanguage: (LanguageMode) -> Unit,
    onNavigateToDetail: (WeatherMetricType) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val mainDataUI by viewModel.mainUiState.collectAsState()
    val context = LocalContext.current
    var isSearchExpanded by remember { mutableStateOf(false) }
    var isCompSearchExpanded by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()
    val canScroll = remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(mainDataUI.validationError) {
        mainDataUI.validationError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.handleIntent(MainScreenIntents.CleanValidationError)
        }
    }

    if (mainDataUI.networkError != null) {
        val errorMessage = when (val err = mainDataUI.networkError!!) {
            ApiError.TooManyRequests -> stringResource(R.string.error_too_many_requests)
            is ApiError.BadRequest -> err.reason ?: stringResource(R.string.error_bad_request)
            ApiError.ServerError -> stringResource(R.string.error_server_error)
            ApiError.InvalidResponse -> stringResource(R.string.error_invalid_response)
            ApiError.NoConnection -> stringResource(R.string.error_no_connection)
            ApiError.EmptyResponse -> stringResource(R.string.error_invalid_response)
            is ApiError.Unknown -> stringResource(R.string.error_unknown)
        }
        AlertDialog(
            onDismissRequest = { viewModel.handleIntent(MainScreenIntents.CleanNetworkError) },
            title = { Text(stringResource(R.string.error)) },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(onClick = { viewModel.handleIntent(MainScreenIntents.CleanNetworkError) }) {
                    Text(stringResource(R.string.button_ok))
                }
            }
        )
    }

    var query by remember {
        mutableStateOf(
            if (mainDataUI.settingsBundle.location != null)
                LocationUtils.buildCityFullName(mainDataUI.settingsBundle.location!!) else ""
        )
    }
    var compQuery by remember {
        mutableStateOf(
            if (mainDataUI.comparisonLocation != null)
                LocationUtils.buildCityFullName(mainDataUI.comparisonLocation!!) else ""
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.handleIntent(MainScreenIntents.UseCurrentLocation)
        }
    }

    LaunchedEffect(mainDataUI.settingsBundle.location) {
        if (query.isBlank() || (mainDataUI.weatherData == null && mainDataUI.settingsBundle.location != null)) {
            query = LocationUtils.buildCityFullName(
                mainDataUI.settingsBundle.location ?: return@LaunchedEffect
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .then(if (canScroll.value) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier)
            .fillMaxSize(),
        topBar = {
            if (!isSearchExpanded && !isCompSearchExpanded) {
                CollapsibleTopBar(
                    scrollBehavior = scrollBehavior,
                    onSettingsClick = { showSettingsDialog = true }
                )
            }
        }
    ) { innerPadding ->
        if (showSettingsDialog) {
            SettingsDialog(
                viewModel = hiltViewModel(),
                onApplyTheme = onApplyTheme,
                onChangeLanguage = onChangeLanguage,
                onClearCache = { viewModel.handleIntent(MainScreenIntents.ClearAppCache) },
                onDismiss = { showSettingsDialog = false }
            )
        }
        val topPadding = if (isSearchExpanded || isCompSearchExpanded) 0.dp
        else (innerPadding.calculateTopPadding() - 8.dp).coerceAtLeast(0.dp)
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = topPadding)
                .verticalScroll(scrollState)
        ) {
            val windowInfo = LocalWindowInfo.current
            val density = LocalDensity.current
            val screenHeight = with(density) { windowInfo.containerSize.height.toDp() }

            Column(
                modifier = if (isSearchExpanded || isCompSearchExpanded) Modifier.heightIn(max = screenHeight)
                else Modifier
                    .heightIn(max = screenHeight)
                    .padding(horizontal = 16.dp)
            ) {
                if (!isCompSearchExpanded) {
                    LocationSearch(
                        viewModel = viewModel, query = query,
                        onQueryChange = { query = it },
                        onCitySelected = {
                            query = LocationUtils.buildCityFullName(it)
                            viewModel.handleIntent(MainScreenIntents.UpdateLocation(it))
                            isSearchExpanded = false
                        },
                        isSearchExpanded = isSearchExpanded,
                        onSearchExpandedChange = { isSearchExpanded = it },
                        actions = {
                            IconButton(onClick = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }) {
                                Icon(
                                    Icons.Default.MyLocation,
                                    contentDescription = "Current Location"
                                )
                            }
                            IconButton(onClick = { viewModel.handleIntent(MainScreenIntents.ToggleBookmark) }) {
                                Icon(
                                    imageVector = if (mainDataUI.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (mainDataUI.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }
                            IconButton(onClick = { viewModel.handleIntent(MainScreenIntents.ToggleComparisonMode) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                                    contentDescription = null,
                                    tint = if (mainDataUI.isComparisonMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    )
                }

                AnimatedVisibility(
                    visible = mainDataUI.isComparisonMode && !isSearchExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    LocationSearch(
                        viewModel = viewModel, query = compQuery,
                        onQueryChange = { compQuery = it },
                        onCitySelected = {
                            compQuery = LocationUtils.buildCityFullName(it)
                            viewModel.handleIntent(MainScreenIntents.UpdateComparisonLocation(it))
                            isCompSearchExpanded = false
                        },
                        isSearchExpanded = isCompSearchExpanded,
                        onSearchExpandedChange = { isCompSearchExpanded = it },
                        modifier = Modifier.padding(top = 4.dp),
                        placeholder = stringResource(R.string.compare_with_hint),
                        actions = {
                            IconButton(onClick = {
                                viewModel.handleIntent(MainScreenIntents.RemoveComparison)
                                compQuery = ""
                            }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    )
                }
            }

            if (!isSearchExpanded && !isCompSearchExpanded) {
                YearsRangeSelection(viewModel, mainDataUI.tempDates)
                DatesRangeSection(viewModel, mainDataUI.tempDates)

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
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

                AnimatedVisibility(visible = mainDataUI.bookmarks.isNotEmpty() && !mainDataUI.isComparisonMode) {
                    BookmarksSection(
                        bookmarks = mainDataUI.bookmarks,
                        onSelect = {
                            viewModel.handleIntent(MainScreenIntents.SelectBookmark(it))
                            query = LocationUtils.buildCityFullName(it.location)
                        },
                        onDelete = { viewModel.handleIntent(MainScreenIntents.DeleteBookmark(it)) }
                    )
                }

                AnimatedVisibility(
                    visible = mainDataUI.isLoading,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }

                AnimatedVisibility(
                    visible = mainDataUI.weatherData != null && !mainDataUI.isLoading,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        DateText(
                            mainDataUI.confirmedDates,
                            mainDataUI.isOneDay,
                            mainDataUI.isOneYear
                        )
                        WeatherCards(
                            weatherGraphDataFactory,
                            mainDataUI.confirmedDates,
                            mainDataUI.isOneYear,
                            mainDataUI.weatherMetrics,
                            mainDataUI.settingsBundle,
                            onNavigateToDetail,
                            sharedTransitionScope,
                            animatedContentScope,
                            mainDataUI.comparisonWeatherMetrics,
                            mainDataUI.settingsBundle.location?.name,
                            mainDataUI.comparisonLocation?.name
                        )
                        Row(
                            modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        ) {
                            Text(stringResource(R.string.data_source))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookmarksSection(
    bookmarks: List<Bookmark>,
    onSelect: (Bookmark) -> Unit,
    onDelete: (String) -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = stringResource(R.string.saved_plans),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookmarks, key = { it.id }) { bookmark ->
                Surface(
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                        .combinedClickable(
                            onClick = { onSelect(bookmark) },
                            onLongClick = { onDelete(bookmark.id) }
                        ),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = bookmark.location.name,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${bookmark.dates.startMonth}/${bookmark.dates.startDay} - ${bookmark.dates.endMonth}/${bookmark.dates.endDay}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WeatherCards(
    weatherGraphDataFactory: WeatherGraphDataFactory,
    dates: DatesBundle,
    isOneYear: Boolean,
    weatherMetrics: WeatherMetrics,
    settingsBundle: SettingsBundle,
    onNavigateToDetail: (WeatherMetricType) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    compMetrics: WeatherMetrics? = null,
    mainCityName: String? = null,
    compCityName: String? = null
) {
    val tempUnit =
        stringArrayResource(R.array.temp_unit_choices).toList()[settingsBundle.temperatureUnitMode.toIndex()]
    val speedUnit =
        stringArrayResource(R.array.speed_unit_choices).toList()[settingsBundle.windUnitMode.toIndex()]
    val precipitationUnit =
        stringArrayResource(R.array.precipitation_unit_choices).toList()[settingsBundle.precipitationUnitMode.toIndex()]

    val popUpLabels = DateUtils.generatePopUpLabels(
        dates.start,
        dates.end,
        settingsBundle.languageMode.toLocale()
    )
    val weatherLabels = WeatherGraphLabels(
        max = stringResource(R.string.max),
        avg = stringResource(R.string.avg),
        min = stringResource(R.string.min),
        sunshine = stringResource(R.string.sunshine),
        daylight = stringResource(R.string.daylight),
        wind = stringResource(R.string.wind),
        gusts = stringResource(R.string.gusts)
    )
    val graphData = weatherGraphDataFactory.create(
        weatherMetrics = weatherMetrics, isDotsVisible = settingsBundle.isDotsVisible,
        isEdgesCurved = settingsBundle.isEdgesCurved, isOneYear = isOneYear,
        colors = LocalExtendedColors.current, popUpLabels = popUpLabels, labels = weatherLabels,
        compMetrics = compMetrics, mainCityName = mainCityName, compCityName = compCityName
    )

    with(sharedTransitionScope) {
        Column {
            WeatherGraphLineCard(
                title = stringResource(R.string.temperature),
                unit = tempUnit,
                icon = Icons.Default.Thermostat,
                lineList = listOfNotNull(
                    graphData.maxTemperature,
                    graphData.avgTemperature,
                    graphData.minTemperature
                ),
                dates = popUpLabels,
                startDate = dates.start,
                endDate = dates.end,
                locale = settingsBundle.languageMode.toLocale(),
                theme = settingsBundle.themeMode,
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "TEMPERATURE"),
                    animatedVisibilityScope = animatedContentScope
                ),
                onClick = { onNavigateToDetail(WeatherMetricType.TEMPERATURE) }
            )
            WeatherGraphLineCard(
                title = stringResource(R.string.air_quality), icon = Icons.Default.Eco,
                lineList = listOfNotNull(graphData.airQuality, graphData.airQualityComp),
                dates = popUpLabels, startDate = dates.start, endDate = dates.end,
                locale = settingsBundle.languageMode.toLocale(), theme = settingsBundle.themeMode,
                minIsZero = true,
                valueFormat = 0,
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "AIR_QUALITY"),
                    animatedVisibilityScope = animatedContentScope
                ),
                onClick = { onNavigateToDetail(WeatherMetricType.AIR_QUALITY) }
            )
            WeatherGraphLineCard(
                title = stringResource(R.string.sun_hours), icon = Icons.Default.WbSunny,
                lineList = listOfNotNull(graphData.sunShineDuration, graphData.dayLightDuration),
                dates = popUpLabels, startDate = dates.start, endDate = dates.end,
                locale = settingsBundle.languageMode.toLocale(), theme = settingsBundle.themeMode,
                minIsZero = true,
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "SUNSHINE"),
                    animatedVisibilityScope = animatedContentScope
                ),
                onClick = { onNavigateToDetail(WeatherMetricType.SUNSHINE) }
            )
            WeatherGraphBarsCard(
                title = stringResource(R.string.precipitation),
                unit = precipitationUnit,
                icon = Icons.Default.WaterDrop,
                barGroups = graphData.precipitation,
                dates = popUpLabels,
                startDate = dates.start,
                endDate = dates.end,
                locale = settingsBundle.languageMode.toLocale(),
                theme = settingsBundle.themeMode,
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "PRECIPITATION"),
                    animatedVisibilityScope = animatedContentScope
                ),
                onClick = { onNavigateToDetail(WeatherMetricType.PRECIPITATION) }
            )
            WeatherGraphLineCard(
                title = stringResource(R.string.wind_speed),
                unit = speedUnit,
                icon = Icons.Default.Air,
                lineList = listOfNotNull(graphData.windSpeed, graphData.windGustsSpeed),
                dates = popUpLabels,
                startDate = dates.start,
                endDate = dates.end,
                locale = settingsBundle.languageMode.toLocale(),
                theme = settingsBundle.themeMode,
                minIsZero = true,
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "WIND"),
                    animatedVisibilityScope = animatedContentScope
                ),
                onClick = { onNavigateToDetail(WeatherMetricType.WIND) }
            )
        }
    }
}

@Composable
private fun DateText(dates: DatesBundle, isOneDay: Boolean, isOneYear: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = DateUtils.formatDateRange(
                startDate = dates.start,
                endDate = dates.end,
                isOneDay = isOneDay,
                isOneYear = isOneYear,
                locale = LocalLocale.current.platformLocale,
                singleDayOneYearString = stringResource(R.string.single_day_one_year),
                singleDaySting = stringResource(R.string.single_day_range),
                dateRangeOneYearSting = stringResource(R.string.date_range_one_year),
                dateRangeString = stringResource(R.string.date_range)
            ),
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun YearsRangeSelection(viewModel: MainViewModel, dates: DatesBundle) {
    val yearChoices by remember {
        mutableStateOf(
            (1940..LocalDate.now().minusYears(1).year).toList().reversed()
        )
    }
    Row(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        DropdownPicker(
            label = stringResource(R.string.start_year),
            list = yearChoices,
            selected = dates.start.year,
            onSelected = { viewModel.handleIntent(MainScreenIntents.UpdateStartYear(it)) },
            modifier = Modifier
                .weight(0.5f)
                .padding(end = 8.dp)
        )
        DropdownPicker(
            label = stringResource(R.string.end_year),
            list = yearChoices,
            selected = dates.end.year,
            onSelected = { viewModel.handleIntent(MainScreenIntents.UpdateEndYear(it)) },
            modifier = Modifier
                .weight(0.5f)
                .padding(start = 8.dp)
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun ColumnScope.DatesRangeSection(viewModel: MainViewModel, datesBundle: DatesBundle) {
    val date1 = datesBundle.start
    val date2 = datesBundle.end
    val monthChoices = stringArrayResource(R.array.month_choices).toList()
    val startDayChoices = remember(date1) { (1..date1.lengthOfMonth()).toList() }
    val endDayChoices = remember(date2) { (1..date2.lengthOfMonth()).toList() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .align(Alignment.CenterHorizontally)
    ) {
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
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
                    .zIndex(0f)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                                .padding(end = 8.dp)
                        )
                        DropdownPicker(
                            label = stringResource(R.string.end_month),
                            list = monthChoices,
                            selected = monthChoices[date2.monthValue - 1],
                            onSelected = {
                                viewModel.handleIntent(
                                    MainScreenIntents.UpdateEndMonth(
                                        monthChoices.indexOf(it) + 1
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(start = 8.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 16.dp
                        )
                    ) {
                        DropdownPicker(
                            label = stringResource(R.string.start_day),
                            list = startDayChoices,
                            selected = date1.dayOfMonth,
                            onSelected = {
                                viewModel.handleIntent(
                                    MainScreenIntents.UpdateStartDay(
                                        it
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(end = 8.dp)
                        )
                        DropdownPicker(
                            label = stringResource(R.string.end_day),
                            list = endDayChoices,
                            selected = date2.dayOfMonth,
                            onSelected = { viewModel.handleIntent(MainScreenIntents.UpdateEndDay(it)) },
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
