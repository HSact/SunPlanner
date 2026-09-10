package com.hsact.sunplanner.ui.detailscreen

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hsact.sunplanner.R
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.model.WeatherMetricType
import com.hsact.sunplanner.domain.model.WeatherMetrics
import com.hsact.sunplanner.ui.components.cards.WeatherGraphBarsCard
import com.hsact.sunplanner.ui.components.cards.WeatherGraphDataFactory
import com.hsact.sunplanner.ui.components.cards.WeatherGraphLabels
import com.hsact.sunplanner.ui.components.cards.WeatherGraphLineCard
import com.hsact.sunplanner.ui.theme.LocalExtendedColors
import com.hsact.sunplanner.ui.utils.WeatherCodeMapper
import com.hsact.sunplanner.ui.utils.stringArrayResource
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WeatherDetailScreen(
    viewModel: WeatherDetailViewModel,
    weatherGraphDataFactory: WeatherGraphDataFactory,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val metricTitle = getMetricTitle(uiState.metricType)

    val isOneDay = uiState.startDate.month == uiState.endDate.month &&
            uiState.startDate.dayOfMonth == uiState.endDate.dayOfMonth
    val isOneYear = uiState.startDate.year == uiState.endDate.year

    val handleBack = {
        if ((uiState.selectedYear != null || uiState.displayMode == DetailDisplayMode.TABLE) && !(isOneDay || isOneYear)) {
            viewModel.toggleDisplayMode()
        } else {
            onBack()
        }
    }

    // Intercept back button if we are in year detail OR in table mode for overall stats
    BackHandler(enabled = (uiState.selectedYear != null || uiState.displayMode == DetailDisplayMode.TABLE) && !(isOneDay || isOneYear), onBack = handleBack)

    if (uiState.error != null) {
        val errorMessage = when (val err = uiState.error!!) {
            ApiError.TooManyRequests -> stringResource(R.string.error_too_many_requests)
            is ApiError.BadRequest -> err.reason ?: stringResource(R.string.error_bad_request)
            ApiError.ServerError -> stringResource(R.string.error_server_error)
            ApiError.InvalidResponse -> stringResource(R.string.error_invalid_response)
            ApiError.NoConnection -> stringResource(R.string.error_no_connection)
            ApiError.EmptyResponse -> stringResource(R.string.error_invalid_response)
            is ApiError.Unknown -> stringResource(R.string.error_unknown)
        }
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(R.string.error)) },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text(stringResource(R.string.button_ok))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = metricTitle, style = MaterialTheme.typography.titleMedium)
                        val subtitle = if (uiState.compCityName.isNotEmpty()) {
                            "${uiState.cityName} vs ${uiState.compCityName}"
                        } else {
                            uiState.cityName
                        }
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleDisplayMode) {
                        Icon(
                            imageVector = if (uiState.displayMode == DetailDisplayMode.LIST) Icons.Default.TableRows else Icons.AutoMirrored.Filled.List,
                            contentDescription = "Toggle View"
                        )
                    }
                    IconButton(onClick = {
                        shareWeatherDetail(context, uiState, metricTitle)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.yearlyData.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No data available", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                val tempUnit =
                    stringArrayResource(R.array.temp_unit_choices).toList()[uiState.settings.temperatureUnitMode.toIndex()]
                val speedUnit =
                    stringArrayResource(R.array.speed_unit_choices).toList()[uiState.settings.windUnitMode.toIndex()]
                val precipitationUnit =
                    stringArrayResource(R.array.precipitation_unit_choices).toList()[uiState.settings.precipitationUnitMode.toIndex()]
                val hoursUnit = stringResource(R.string.hours_abbr)

                val displayUnit = when (uiState.metricType) {
                    WeatherMetricType.TEMPERATURE -> tempUnit
                    WeatherMetricType.SUNSHINE -> hoursUnit
                    WeatherMetricType.PRECIPITATION -> precipitationUnit
                    WeatherMetricType.WIND -> speedUnit
                    WeatherMetricType.AIR_QUALITY -> ""
                }

                val weatherLabels = WeatherGraphLabels(
                    max = stringResource(R.string.max),
                    avg = stringResource(R.string.avg),
                    min = stringResource(R.string.min),
                    sunshine = stringResource(R.string.sunshine),
                    daylight = stringResource(R.string.daylight),
                    wind = stringResource(R.string.wind),
                    gusts = stringResource(R.string.gusts)
                )
                val animatedIndices = remember { mutableSetOf<Int>() }
                val yearsChoices =
                    remember(uiState.yearlyData) { uiState.yearlyData.map { it.year } }
                val locale = LocalLocale.current.platformLocale

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item(key = "SUMMARY", contentType = "HEADER") {
                        with(sharedTransitionScope) {
                            Box(
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = uiState.metricType.name),
                                    animatedVisibilityScope = animatedContentScope
                                )
                            ) {
                                Column(modifier = Modifier.animateContentSize()) {
                                    AnimatedVisibility(
                                        visible = uiState.isMainVisible,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        SummaryHeader(
                                            summary = uiState.summary,
                                            unit = displayUnit,
                                            cityName = if (uiState.compCityName.isNotEmpty()) uiState.cityName else null,
                                            isInteger = uiState.metricType == WeatherMetricType.AIR_QUALITY,
                                            onClick = { if (uiState.displayMode == DetailDisplayMode.LIST) viewModel.selectYear(null) }
                                        )
                                    }
                                    AnimatedVisibility(
                                        visible = uiState.isCompVisible && uiState.compCityName.isNotEmpty(),
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        SummaryHeader(
                                            summary = uiState.compSummary,
                                            unit = displayUnit,
                                            cityName = uiState.compCityName,
                                            isInteger = uiState.metricType == WeatherMetricType.AIR_QUALITY,
                                            onClick = { if (uiState.displayMode == DetailDisplayMode.LIST) viewModel.selectYear(null) }
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = uiState.selectedYear == null && uiState.displayMode == DetailDisplayMode.LIST,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        val colors = LocalExtendedColors.current
                                        
                                        val startMD = uiState.startDate.monthValue * 100 + uiState.startDate.dayOfMonth
                                        val endMD = uiState.endDate.monthValue * 100 + uiState.endDate.dayOfMonth
                                        val isWrapping = startMD > endMD
                                        
                                        val normalizedStart = uiState.startDate.withYear(2024)
                                        val normalizedEnd = if (!isWrapping) uiState.endDate.withYear(2024) else uiState.endDate.withYear(2025)

                                        val shouldAnimateSummary = remember {
                                            val firstTime = !animatedIndices.contains(-1)
                                            if (firstTime) animatedIndices.add(-1)
                                            firstTime
                                        }

                                        val popUpLabels = remember(uiState.overallMetrics, uiState.compOverallMetrics) {
                                            DateUtils.generatePopUpLabels(
                                                uiState.startDate,
                                                uiState.endDate,
                                                uiState.settings.languageMode.toLocale()
                                            )
                                        }
                                        val overallGraphData = remember(
                                            uiState.overallMetrics,
                                            uiState.compOverallMetrics,
                                            uiState.isMainVisible,
                                            uiState.isCompVisible,
                                            uiState.settings,
                                            colors
                                        ) {
                                            weatherGraphDataFactory.create(
                                                weatherMetrics = if (uiState.isMainVisible) uiState.overallMetrics else null,
                                                compMetrics = if (uiState.isCompVisible) uiState.compOverallMetrics else null,
                                                isDotsVisible = uiState.settings.isDotsVisible,
                                                isEdgesCurved = uiState.settings.isEdgesCurved,
                                                isOneYear = !isWrapping,
                                                colors = colors,
                                                popUpLabels = popUpLabels,
                                                labels = weatherLabels,
                                                mainCityName = uiState.cityName,
                                                compCityName = uiState.compCityName
                                            )
                                        }

                                        when (uiState.metricType) {
                                            WeatherMetricType.TEMPERATURE -> {
                                                WeatherGraphLineCard(
                                                    title = stringResource(R.string.average_stats),
                                                    unit = tempUnit,
                                                    icon = Icons.Default.Thermostat,
                                                    lineList = listOfNotNull(
                                                        overallGraphData.maxTemperature,
                                                        overallGraphData.avgTemperature,
                                                        overallGraphData.minTemperature
                                                    ),
                                                    dates = popUpLabels,
                                                    startDate = normalizedStart,
                                                    endDate = normalizedEnd,
                                                    locale = uiState.settings.languageMode.toLocale(),
                                                    theme = uiState.settings.themeMode,
                                                    animate = shouldAnimateSummary,
                                                    onClick = { viewModel.selectYear(null) }
                                                )
                                            }

                                            WeatherMetricType.SUNSHINE -> {
                                                WeatherGraphLineCard(
                                                    title = stringResource(R.string.average_stats),
                                                    icon = Icons.Default.WbSunny,
                                                    lineList = listOfNotNull(
                                                        overallGraphData.sunShineDuration,
                                                        overallGraphData.dayLightDuration
                                                    ),
                                                    dates = popUpLabels,
                                                    startDate = normalizedStart,
                                                    endDate = normalizedEnd,
                                                    locale = uiState.settings.languageMode.toLocale(),
                                                    theme = uiState.settings.themeMode,
                                                    minIsZero = true,
                                                    animate = shouldAnimateSummary,
                                                    onClick = { viewModel.selectYear(null) }
                                                )
                                            }

                                            WeatherMetricType.PRECIPITATION -> {
                                                WeatherGraphBarsCard(
                                                    title = stringResource(R.string.average_stats),
                                                    unit = precipitationUnit,
                                                    icon = Icons.Default.WaterDrop,
                                                    barGroups = overallGraphData.precipitation,
                                                    dates = popUpLabels,
                                                    startDate = normalizedStart,
                                                    endDate = normalizedEnd,
                                                    locale = uiState.settings.languageMode.toLocale(),
                                                    theme = uiState.settings.themeMode,
                                                    animate = shouldAnimateSummary,
                                                    onClick = { viewModel.selectYear(null) }
                                                )
                                            }

                                            WeatherMetricType.WIND -> {
                                                WeatherGraphLineCard(
                                                    title = stringResource(R.string.average_stats),
                                                    unit = speedUnit,
                                                    icon = Icons.Default.Air,
                                                    lineList = listOfNotNull(
                                                        overallGraphData.windSpeed,
                                                        overallGraphData.windGustsSpeed
                                                    ),
                                                    dates = popUpLabels,
                                                    startDate = normalizedStart,
                                                    endDate = normalizedEnd,
                                                    locale = uiState.settings.languageMode.toLocale(),
                                                    theme = uiState.settings.themeMode,
                                                    minIsZero = true,
                                                    animate = shouldAnimateSummary,
                                                    onClick = { viewModel.selectYear(null) }
                                                )
                                            }

                                            WeatherMetricType.AIR_QUALITY -> {
                                                WeatherGraphLineCard(
                                                    title = stringResource(R.string.average_stats),
                                                    icon = Icons.Default.Eco,
                                                    lineList = listOfNotNull(
                                                        overallGraphData.airQuality,
                                                        overallGraphData.airQualityComp
                                                    ),
                                                    dates = popUpLabels,
                                                    startDate = normalizedStart,
                                                    endDate = normalizedEnd,
                                                    locale = uiState.settings.languageMode.toLocale(),
                                                    theme = uiState.settings.themeMode,
                                                    minIsZero = true,
                                                    animate = shouldAnimateSummary,
                                                    valueFormat = 0,
                                                    onClick = { viewModel.selectYear(null) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item(key = "INSIGHTS", contentType = "HEADER") {
                        AnimatedVisibility(
                            visible = uiState.selectedYear == null && uiState.insights.isNotEmpty() && uiState.displayMode == DetailDisplayMode.LIST,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            InsightsSection(uiState.insights)
                        }
                    }

                    item(key = "FILTER", contentType = "HEADER") {
                        Column {
                            if (uiState.compCityName.isNotEmpty() || (uiState.displayMode == DetailDisplayMode.TABLE && !isOneDay)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (uiState.compCityName.isNotEmpty()) {
                                        FilterChip(
                                            selected = uiState.isMainVisible,
                                            onClick = viewModel::toggleMainVisibility,
                                            label = { Text(uiState.cityName, maxLines = 1) },
                                            leadingIcon = if (uiState.isMainVisible) {
                                                {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        null,
                                                        Modifier.size(16.dp)
                                                    )
                                                }
                                            } else null,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        FilterChip(
                                            selected = uiState.isCompVisible,
                                            onClick = viewModel::toggleCompVisibility,
                                            label = { Text(uiState.compCityName, maxLines = 1) },
                                            leadingIcon = if (uiState.isCompVisible) {
                                                {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        null,
                                                        Modifier.size(16.dp)
                                                    )
                                                }
                                            } else null,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                    }

                                    if (uiState.displayMode == DetailDisplayMode.TABLE && !isOneDay && uiState.yearlyData.size > 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                        val currentYearLabel =
                                            uiState.selectedYear?.toString()
                                                ?: stringResource(R.string.all_years)
                                        var expanded by remember { mutableStateOf(false) }

                                        Box {
                                            FilterChip(
                                                selected = uiState.selectedYear != null,
                                                onClick = { expanded = true },
                                                label = { Text(currentYearLabel) },
                                                trailingIcon = {
                                                    Icon(
                                                        Icons.Default.ArrowDropDown,
                                                        null
                                                    )
                                                }
                                            )
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }) {
                                                DropdownMenuItem(
                                                    text = { Text(stringResource(R.string.all_years)) },
                                                    onClick = {
                                                        viewModel.selectYear(null); expanded = false
                                                    }
                                                )
                                                yearsChoices.forEach { year ->
                                                    DropdownMenuItem(
                                                        text = { Text(year.toString()) },
                                                        onClick = {
                                                            viewModel.selectYear(year); expanded =
                                                            false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.displayMode == DetailDisplayMode.LIST) {
                        yearlyDataItems(
                            uiState = uiState,
                            weatherGraphDataFactory = weatherGraphDataFactory,
                            tempUnit = tempUnit,
                            speedUnit = speedUnit,
                            precUnit = precipitationUnit,
                            weatherLabels = weatherLabels,
                            animatedIndices = animatedIndices,
                            onYearClick = viewModel::selectYear
                        )
                    } else {
                        tableDataItems(
                            uiState = uiState,
                            tempUnit = tempUnit,
                            speedUnit = speedUnit,
                            precUnit = precipitationUnit,
                            hoursUnit = hoursUnit,
                            isOneDay = isOneDay,
                            locale = locale
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationFilterSection(
    mainName: String, compName: String,
    mainVisible: Boolean, compVisible: Boolean,
    onToggleMain: () -> Unit, onToggleComp: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = mainVisible, onClick = onToggleMain,
            label = { Text(mainName, maxLines = 1) },
            leadingIcon = if (mainVisible) {
                { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
            } else null
        )
        FilterChip(
            selected = compVisible, onClick = onToggleComp,
            label = { Text(compName, maxLines = 1) },
            leadingIcon = if (compVisible) {
                { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
            } else null
        )
    }
}

private fun shareWeatherDetail(context: Context, uiState: WeatherDetailUiState, title: String) {
    val sb = StringBuilder()
    sb.append(context.getString(R.string.share_report_title, title)).append("\n")
    sb.append(context.getString(R.string.share_location, uiState.cityName)).append("\n")
    if (uiState.compCityName.isNotEmpty()) {
        sb.append(context.getString(R.string.share_comparison, uiState.compCityName)).append("\n")
    }
    sb.append(context.getString(R.string.share_period, uiState.startDate, uiState.endDate))
        .append("\n\n")
    if (uiState.insights.isNotEmpty()) {
        sb.append(context.getString(R.string.share_insights)).append("\n")
        uiState.insights.forEach { sb.append("• $it\n") }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.share_chooser_title)
        )
    )
}

@Composable
private fun InsightsSection(insights: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        insights.forEach { insight ->
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (insight.contains("vs") || insight.contains("Historically")) Icons.AutoMirrored.Filled.CompareArrows else Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = insight,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryHeader(summary: WeatherDetailSummary, unit: String, cityName: String? = null, isInteger: Boolean = false, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (cityName != null) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryItem(
                stringResource(R.string.record_high),
                summary.maxValue,
                unit,
                Modifier.weight(1f),
                MaterialTheme.colorScheme.errorContainer,
                isInteger = isInteger,
                onClick = onClick
            )
            SummaryItem(
                stringResource(R.string.average_stats),
                summary.avgValue,
                unit,
                Modifier.weight(1f),
                MaterialTheme.colorScheme.primaryContainer,
                isInteger = isInteger,
                onClick = onClick
            )
            SummaryItem(
                stringResource(R.string.record_low),
                summary.minValue,
                unit,
                Modifier.weight(1f),
                MaterialTheme.colorScheme.tertiaryContainer,
                isInteger = isInteger,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: Double,
    unit: String,
    modifier: Modifier,
    color: Color,
    isInteger: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = color, onClick = onClick) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            val formattedValue = if (isInteger) {
                value.roundToInt().toString()
            } else {
                "${(value * 10).roundToInt() / 10.0}"
            }
            Text(
                text = "$formattedValue${if (unit.isNotEmpty()) " $unit" else ""}",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun LazyListScope.yearlyDataItems(
    uiState: WeatherDetailUiState,
    weatherGraphDataFactory: WeatherGraphDataFactory,
    tempUnit: String,
    speedUnit: String,
    precUnit: String,
    weatherLabels: WeatherGraphLabels,
    animatedIndices: MutableSet<Int>,
    onYearClick: (Int) -> Unit
) {
    itemsIndexed(
        items = uiState.yearlyData,
        key = { _, data -> data.year },
        contentType = { _, _ -> "YEARLY_GRAPH" }
    ) { index, data ->
        val compData = uiState.compYearlyData.find { it.year == data.year }

        val shouldAnimate = remember(index) {
            val firstTime = !animatedIndices.contains(index)
            if (firstTime) animatedIndices.add(index)
            firstTime
        }

        val popUpLabels = remember(data) {
            data.dateLabels.map { dateStr ->
                val date = LocalDate.parse(dateStr)
                val month = date.month.getDisplayName(
                    TextStyle.SHORT,
                    uiState.settings.languageMode.toLocale()
                )
                "${date.dayOfMonth} $month"
            }
        }

            val colors = LocalExtendedColors.current
            val graphData = remember(
                data,
                uiState.isMainVisible,
                uiState.isCompVisible,
                uiState.settings,
                colors
            ) {
                weatherGraphDataFactory.create(
                    weatherMetrics = if (uiState.isMainVisible) data.metrics else null,
                    compMetrics = if (uiState.isCompVisible) compData?.metrics else null,
                    isDotsVisible = uiState.settings.isDotsVisible,
                    isEdgesCurved = uiState.settings.isEdgesCurved, isOneYear = true,
                    colors = colors, popUpLabels = popUpLabels, labels = weatherLabels,
                    mainCityName = uiState.cityName, compCityName = uiState.compCityName
                )
            }

        val yearTitle = "${data.year} ${stringResource(R.string.year_suffix)}"
        
        val startMD = uiState.startDate.monthValue * 100 + uiState.startDate.dayOfMonth
        val endMD = uiState.endDate.monthValue * 100 + uiState.endDate.dayOfMonth
        val isWrapping = startMD > endMD
        
        val yearStart = uiState.startDate.withYear(data.year)
        val yearEnd = if (!isWrapping) uiState.endDate.withYear(data.year) else uiState.endDate.withYear(data.year + 1)

        Box(modifier = Modifier.padding(top = 16.dp)) {
            if (uiState.isMainVisible || uiState.isCompVisible) {
                when (uiState.metricType) {
                    WeatherMetricType.TEMPERATURE -> {
                        WeatherGraphLineCard(
                            title = yearTitle,
                            unit = tempUnit,
                            icon = Icons.Default.Thermostat,
                            lineList = listOfNotNull(
                                graphData.maxTemperature,
                                graphData.avgTemperature,
                                graphData.minTemperature
                            ),
                            dates = popUpLabels,
                            startDate = yearStart,
                            endDate = yearEnd,
                            locale = uiState.settings.languageMode.toLocale(),
                            theme = uiState.settings.themeMode,
                            animate = shouldAnimate,
                            onClick = { onYearClick(data.year) }
                        )
                    }

                    WeatherMetricType.SUNSHINE -> {
                        WeatherGraphLineCard(
                            title = yearTitle,
                            icon = Icons.Default.WbSunny,
                            lineList = listOfNotNull(
                                graphData.sunShineDuration,
                                graphData.dayLightDuration
                            ),
                            dates = popUpLabels,
                            startDate = yearStart,
                            endDate = yearEnd,
                            locale = uiState.settings.languageMode.toLocale(),
                            theme = uiState.settings.themeMode,
                            minIsZero = true,
                            animate = shouldAnimate,
                            onClick = { onYearClick(data.year) }
                        )
                    }

                    WeatherMetricType.PRECIPITATION -> {
                                                WeatherGraphBarsCard(
                                                    title = yearTitle,
                                                    unit = precUnit,
                                                    icon = Icons.Default.WaterDrop,
                                                    barGroups = graphData.precipitation,
                                                    dates = popUpLabels,
                                                    startDate = yearStart,
                                                    endDate = yearEnd,
                                                    locale = uiState.settings.languageMode.toLocale(),
                                                    theme = uiState.settings.themeMode,
                                                    animate = shouldAnimate,
                                                    onClick = { onYearClick(data.year) }
                                                )
                    }

                    WeatherMetricType.WIND -> {
                        WeatherGraphLineCard(
                            title = yearTitle,
                            unit = speedUnit,
                            icon = Icons.Default.Air,
                            lineList = listOfNotNull(
                                graphData.windSpeed,
                                graphData.windGustsSpeed
                            ),
                            dates = popUpLabels,
                            startDate = yearStart,
                            endDate = yearEnd,
                            locale = uiState.settings.languageMode.toLocale(),
                            theme = uiState.settings.themeMode,
                            minIsZero = true,
                            animate = shouldAnimate,
                            onClick = { onYearClick(data.year) }
                        )
                    }

                    WeatherMetricType.AIR_QUALITY -> {
                        WeatherGraphLineCard(
                            title = yearTitle,
                            icon = Icons.Default.Eco,
                            lineList = listOfNotNull(
                                graphData.airQuality,
                                graphData.airQualityComp
                            ),
                            dates = popUpLabels,
                            startDate = yearStart,
                            endDate = yearEnd,
                            locale = uiState.settings.languageMode.toLocale(),
                            theme = uiState.settings.themeMode,
                            minIsZero = true,
                            animate = shouldAnimate,
                            valueFormat = 0,
                            onClick = { onYearClick(data.year) }
                        )
                    }
                }
            }
        }
    }
}

private fun LazyListScope.tableDataItems(
    uiState: WeatherDetailUiState,
    tempUnit: String,
    speedUnit: String,
    precUnit: String,
    hoursUnit: String,
    isOneDay: Boolean,
    locale: Locale
) {
    val isComparison =
        uiState.compCityName.isNotEmpty() && (uiState.isMainVisible && uiState.isCompVisible)

    item { Spacer(modifier = Modifier.height(8.dp)) }

    item {
        if (isComparison) TableHeaderComp(uiState.cityName, uiState.compCityName)
        else TableHeader(isOneDay && uiState.selectedYear == null)
    }

    if (isOneDay && uiState.selectedYear == null) {
        itemsIndexed(uiState.yearlyData) { index, data ->
            val compData = uiState.compYearlyData.find { it.year == data.year }
            if (uiState.isMainVisible || uiState.isCompVisible) {
                TableRow(
                    label = data.year.toString(),
                    metrics = if (uiState.isMainVisible) data.metrics else (compData?.metrics
                        ?: data.metrics),
                    dataIndex = 0,
                    metricType = uiState.metricType, isEven = index % 2 == 0, isOneDay = true,
                    locale = locale, tempUnit = tempUnit, speedUnit = speedUnit,
                    precUnit = precUnit, hoursUnit = hoursUnit,
                    compMetrics = if (isComparison) compData?.metrics else null
                )
            }
        }
    } else {
        val targetYear = uiState.selectedYear
        if (targetYear == null) {
            // Show average data across years
            uiState.overallMetrics?.let { mainMetrics ->
                itemsIndexed(mainMetrics.dateLabels) { index, dateLabel ->
                    if (uiState.isMainVisible || uiState.isCompVisible) {
                        TableRow(
                            label = dateLabel,
                            metrics = if (uiState.isMainVisible) mainMetrics else (uiState.compOverallMetrics
                                ?: mainMetrics),
                            dataIndex = index,
                            metricType = uiState.metricType,
                            isEven = index % 2 == 0,
                            isOneDay = false,
                            locale = locale,
                            tempUnit = tempUnit,
                            speedUnit = speedUnit,
                            precUnit = precUnit,
                            hoursUnit = hoursUnit,
                            compMetrics = if (isComparison) uiState.compOverallMetrics else null,
                            isAggregated = true
                        )
                    }
                }
            }
        } else {
            val data = uiState.yearlyData.find { it.year == targetYear }
            data?.let {
                itemsIndexed(it.dateLabels) { index, dateLabel ->
                    val compData = uiState.compYearlyData.find { it.year == targetYear }
                    if (uiState.isMainVisible || uiState.isCompVisible) {
                        TableRow(
                            label = dateLabel,
                            metrics = if (uiState.isMainVisible) it.metrics else (compData?.metrics
                                ?: it.metrics),
                            dataIndex = index,
                            metricType = uiState.metricType,
                            isEven = index % 2 == 0,
                            isOneDay = false,
                            locale = locale,
                            tempUnit = tempUnit,
                            speedUnit = speedUnit,
                            precUnit = precUnit,
                            hoursUnit = hoursUnit,
                            compMetrics = if (isComparison) compData?.metrics else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeader(isOneDay: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
    ) {
        Text(
            text = if (isOneDay) stringResource(R.string.year_label) else stringResource(R.string.date_label),
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.value_label),
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
        Text(
            text = "",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TableHeaderComp(mainCity: String, compCity: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.date_label),
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = mainCity,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            maxLines = 1
        )
        Text(
            text = compCity,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            maxLines = 1
        )
    }
}

@Composable
private fun TableRow(
    label: String, metrics: WeatherMetrics, dataIndex: Int,
    metricType: WeatherMetricType, isEven: Boolean, isOneDay: Boolean, locale: Locale,
    tempUnit: String, speedUnit: String, precUnit: String, hoursUnit: String,
    compMetrics: WeatherMetrics? = null,
    isAggregated: Boolean = false
) {
    val backgroundColor =
        if (isEven) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(
            alpha = 0.3f
        )
    val weatherCode = metrics.weatherCodes.getOrNull(dataIndex) ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isOneDay) label else {
                if (isAggregated) {
                    // label is "MM-dd"
                    val parts = label.split("-")
                    val day = parts.getOrNull(1)?.toInt() ?: 1
                    val month = parts.getOrNull(0)?.toInt() ?: 1
                    val date = LocalDate.of(2024, month, day)
                    "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.SHORT, locale)}"
                } else {
                    val date = LocalDate.parse(label)
                    "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.SHORT, locale)}"
                }
            },
            modifier = Modifier.weight(if (compMetrics != null) 1.2f else 1.5f),
            style = MaterialTheme.typography.bodyMedium
        )

        val valueText =
            formatValue(metrics, dataIndex, metricType, tempUnit, hoursUnit, precUnit, speedUnit)
        Text(
            text = valueText,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )

        if (compMetrics != null) {
            val compValueText = formatValue(
                compMetrics,
                dataIndex,
                metricType,
                tempUnit,
                hoursUnit,
                precUnit,
                speedUnit
            )
            Text(
                text = compValueText,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )
        } else {
            Icon(
                imageVector = WeatherCodeMapper.getIcon(weatherCode), contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier
                    .weight(1f)
                    .size(20.dp)
            )
        }
    }
}

private fun formatValue(
    m: WeatherMetrics,
    idx: Int,
    type: WeatherMetricType,
    tU: String,
    hU: String,
    pU: String,
    sU: String
): String {
    return when (type) {
        WeatherMetricType.TEMPERATURE -> "${m.maxTemps.getOrNull(idx) ?: 0.0}$tU"
        WeatherMetricType.SUNSHINE -> "${m.sunshine.getOrNull(idx) ?: 0.0} $hU"
        WeatherMetricType.PRECIPITATION -> "${m.precipitation.getOrNull(idx) ?: 0.0} $pU"
        WeatherMetricType.WIND -> "${m.windSpeed.getOrNull(idx) ?: 0.0} $sU"
        WeatherMetricType.AIR_QUALITY -> "${(m.airQuality.getOrNull(idx) ?: 0.0).roundToInt()}"
    }
}

@Composable
private fun getMetricTitle(type: WeatherMetricType): String {
    return when (type) {
        WeatherMetricType.TEMPERATURE -> stringResource(R.string.temperature)
        WeatherMetricType.SUNSHINE -> stringResource(R.string.sun_hours)
        WeatherMetricType.PRECIPITATION -> stringResource(R.string.precipitation)
        WeatherMetricType.WIND -> stringResource(R.string.wind_speed)
        WeatherMetricType.AIR_QUALITY -> stringResource(R.string.air_quality)
    }
}
