package com.hsact.sunplanner.ui.components.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.domain.model.ThemeMode
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import java.time.LocalDate
import java.util.Locale

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun WeatherGraphBarsCard(
    title: String,
    barGroups: List<Bars>,
    dates: List<String>,
    startDate: LocalDate,
    endDate: LocalDate,
    locale: Locale,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    unit: String? = null,
    theme: ThemeMode = ThemeMode.SYSTEM,
    animate: Boolean = true,
    onClick: () -> Unit = {}
) {
    val max = remember(barGroups) {
        val allValues = barGroups.flatMap { it.values.map { data -> data.value } }
        allValues.maxOrNull() ?: 0.0
    }

    val isDarkTheme =
        if (theme == ThemeMode.SYSTEM) isSystemInDarkTheme()
        else {
            theme == ThemeMode.DARK
        }
    val textStyle = remember(isDarkTheme) {
        if (isDarkTheme) TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
        else TextStyle(color = Color.Black)
    }

    // We disable the library's built-in label helper (legend) and draw it manually for better control
    val labelHelperProperties = LabelHelperProperties(enabled = false)

    val gridProperties = GridProperties(enabled = false)

    val indicatorProperties = HorizontalIndicatorProperties(
        enabled = true,
        textStyle = textStyle,
    )

    val popupProperties = PopupProperties(
        textStyle = TextStyle.Default.copy(fontSize = 12.sp, color = Color.White),
        contentBuilder = { popup ->
            val numGroups = barGroups.size
            val originalIndex = popup.valueIndex / numGroups
            val groupIndex = popup.valueIndex % numGroups

            val date = dates.getOrNull(originalIndex) ?: ""
            val group = barGroups.getOrNull(groupIndex)
            val valStr = popup.value.format(1)
            val unitStr = if (unit != null) " $unit" else ""
            val labelPrefix = if (group?.label?.isNotBlank() == true) "${group.label}: " else ""

            "$labelPrefix$valStr$unitStr\n$date"
        }
    )

    ElevatedCard(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        onClick = onClick
    ) {
        BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
            val numOriginalPoints = if (barGroups.isNotEmpty()) barGroups.first().values.size else 0
            val numGroups = barGroups.size

            // Interleave logic: city1 day1, city2 day1, city1 day2...
            // We pad with zero-height bars to keep them side-by-side
            val processedData = if (numGroups > 1) {
                barGroups.mapIndexed { groupIdx, group ->
                    val interleavedValues = mutableListOf<Bars.Data>()
                    val seriesColor = group.values.firstOrNull()?.color ?: SolidColor(Color.Gray)
                    for (i in 0 until numOriginalPoints) {
                        for (j in 0 until numGroups) {
                            if (j == groupIdx) {
                                interleavedValues.add(group.values[i])
                            } else {
                                interleavedValues.add(
                                    group.values[i].copy(
                                        value = 0.0,
                                        color = seriesColor
                                    )
                                )
                            }
                        }
                    }
                    Bars(label = group.label, values = interleavedValues)
                }
            } else {
                barGroups
            }

            val totalInterleavedPoints =
                if (processedData.isNotEmpty()) processedData.first().values.size else 0

            var axisLabels = DateUtils.generateAxisXLabels(
                startDate = startDate,
                endDate = endDate,
                locale = locale
            )

            if (numGroups > 1) {
                axisLabels = axisLabels.flatMap { listOf(it) + List(numGroups - 1) { "" } }
            }

            val density = LocalDensity.current
            val screenWidthPx = with(density) { maxWidth.toPx() }
            val totalWidth = totalTextWidth(axisLabels, textStyle)
            axisLabels =
                DateUtils.reduceAxisXLabels(axisLabels, totalWidth, screenWidthPx.toDouble())
            if (axisLabels.size < 2) {
                axisLabels = axisLabels + axisLabels
            }
            val labelProperties = LabelProperties(
                enabled = true,
                textStyle = textStyle,
                labels = axisLabels,
                rotation = LabelProperties.Rotation(degree = 0f)
            )

            val spacing = 2.dp
            val barThickness = if (totalInterleavedPoints > 0) {
                (maxWidth - (16 * 2).dp - (spacing * (totalInterleavedPoints - 1))) / totalInterleavedPoints
            } else 15.dp

            val barProperties = BarProperties(
                thickness = barThickness.coerceAtLeast(2.dp),
                spacing = spacing,
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 4.dp, topLeft = 4.dp),
            )
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Column {
                    WeatherCardHeader(title = title, unit = unit, icon = icon)

                    // Manual Legend
                    if (numGroups > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            barGroups.forEach { group ->
                                val brush =
                                    group.values.firstOrNull()?.color ?: SolidColor(Color.Gray)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(brush)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = group.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    ColumnChart(
                        modifier = Modifier
                            .heightIn(max = 300.dp),
                        data = processedData,
                        barProperties = barProperties,
                        animationMode =
                            if (animate && numOriginalPoints < 100) AnimationMode.Together(
                                delayBuilder = { it * 10L })
                            else AnimationMode.None,
                        gridProperties = gridProperties,
                        indicatorProperties = indicatorProperties,
                        labelHelperProperties = labelHelperProperties,
                        labelProperties = labelProperties,
                        popupProperties = popupProperties,
                        maxValue = max
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardPreview() {
    val barData = listOf(
        Bars.Data(value = 4.0, color = SolidColor(Color(0xFF4CAF50))),
        Bars.Data(value = 7.0, color = SolidColor(Color(0xFF2196F3))),
        Bars.Data(value = 10.0, color = SolidColor(Color(0xFFFF9800))),
    )

    val previewBars = Bars(
        label = "Avg Temp",
        values = barData
    )

    WeatherGraphBarsCard(
        title = "Average Temperature",
        barGroups = listOf(previewBars),
        dates = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
        startDate = LocalDate.now().minusDays(10),
        endDate = LocalDate.now(),
        locale = LocalLocale.current.platformLocale
    )
}
