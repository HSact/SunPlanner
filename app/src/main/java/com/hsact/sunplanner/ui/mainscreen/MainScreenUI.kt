package com.hsact.sunplanner.ui.mainscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.LocaleList
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
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
import com.hsact.sunplanner.ui.mainscreen.searchUiKit.SearchUI
import java.time.LocalDate
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hsact.sunplanner.ui.settings.LanguageMode
import com.hsact.sunplanner.ui.settings.SettingsDialog
import com.hsact.sunplanner.ui.settings.ThemeMode
import java.util.Locale

class MainScreenUI(val viewModel: MainViewModel) {
    @SuppressLint("LocalContextConfigurationRead")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(modifier: Modifier = Modifier, onApplyTheme: (ThemeMode) -> Unit) {
        val mainDataUI by viewModel.searchDataUI.collectAsState()
        val context = LocalContext.current
        LaunchedEffect(mainDataUI.languageMode) {
            val locale = when (mainDataUI.languageMode) {
                LanguageMode.ENGLISH -> Locale("en")
                LanguageMode.RUSSIAN -> Locale("ru")
            }
            updateAppLocale2(context, locale)
            /*val locale = when (mainDataUI.languageMode) {
                LanguageMode.ENGLISH -> Locale("en")
                LanguageMode.RUSSIAN -> Locale("ru")
            }
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)*/
        }
        var showSettingsDialog by remember { mutableStateOf(false) }
        var cityName by remember { mutableStateOf("") }
        var isSearchExpanded by remember { mutableStateOf(false) }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val scrollState = rememberScrollState()
        val canScroll = remember { mutableStateOf(false) }
        val searchUI = SearchUI()
        var query by remember { mutableStateOf("") }
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
        val localLocale = staticCompositionLocalOf { Locale.getDefault() }
        var locale by remember { mutableStateOf(Locale.getDefault()) }
        val currentLocale by rememberUpdatedState(locale)
        CompositionLocalProvider(localLocale provides localLocale.current) {

            Scaffold(
                modifier = Modifier
                    .then(if (canScroll.value) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier)
                    .fillMaxSize(),
                topBar = {
                    if (!isSearchExpanded) {
                        TopAppBar(
                            title = {
                                Text(
                                    stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            actions = {
                                IconButton(onClick = { showSettingsDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings"
                                    )
                                }
                            },
                            scrollBehavior = if (canScroll.value) scrollBehavior else null
                        )
                    }
                }
            ) { innerPadding ->
                if (showSettingsDialog) {
                    SettingsDialog(hiltViewModel(), onApplyTheme).ShowDialog {
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
                                onClick = { viewModel.onSearchClick() },
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
                            Row(
                                modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = DateUtils.formatDateRange(
                                        startDate = mainDataUI.confirmedStartLD,
                                        endDate = mainDataUI.confirmedEndLD,
                                        isOneDay = mainDataUI.isOneDay,
                                        locale = Locale.getDefault(),
                                        singleDaySting = stringResource(R.string.single_day_range),
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
                                    stringResource(R.string.temperature),
                                    listOf(
                                        mainDataUI.maxTemperature!!,
                                        mainDataUI.minTemperature!!
                                    ),
                                    mainDataUI.confirmedStartLD,
                                    mainDataUI.confirmedEndLD
                                )
                            }
                            Row(modifier.fillMaxWidth())
                            {
                                WeatherGraphLineCard().WeatherCard(
                                    stringResource(R.string.sunshine_hours),
                                    listOf(mainDataUI.sunDuration!!),
                                    mainDataUI.confirmedStartLD,
                                    mainDataUI.confirmedEndLD,
                                    true              //set min value 0
                                )
                            }
                            Row(modifier.fillMaxWidth())
                            {
                                WeatherGraphBarsLineCard().WeatherCard(
                                    stringResource(R.string.precipitation),
                                    listOf(mainDataUI.precipitation!!),
                                    mainDataUI.confirmedStartLD,
                                    mainDataUI.confirmedEndLD
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
    fun updateAppLocale(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
        context.createConfigurationContext(configuration)
    }
    fun updateAppLocale2(context: Context, locale: Locale) {
        // Устанавливаем локаль по умолчанию
        Locale.setDefault(locale)

        // Получаем текущие ресурсы и конфигурацию
        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        // Применяем новую конфигурацию
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
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