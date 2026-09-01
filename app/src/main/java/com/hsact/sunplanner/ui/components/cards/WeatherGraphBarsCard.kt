package com.hsact.sunplanner.ui.components.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    icon: ImageVector? = null,
    unit: String? = null,
    theme: ThemeMode = ThemeMode.SYSTEM
) {
    val max = remember(barGroups) {
        val allValues = barGroups.flatMap { it.values.map { data -> data.value } }
        allValues.maxOrNull() ?: 0.0
    }

    val hasAnyLabel = remember(barGroups) {
        barGroups.any { it.label.isNotBlank() }
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

    val labelHelperProperties = LabelHelperProperties(
        enabled = hasAnyLabel,
        textStyle = textStyle
    )

    val gridProperties = GridProperties(enabled = false)

    val indicatorProperties = HorizontalIndicatorProperties(
        enabled = true,
        textStyle = textStyle,
    )

    val popupProperties = PopupProperties(
        textStyle = TextStyle.Default.copy(fontSize = 12.sp, color = Color.White),
        contentBuilder = { popup ->
            val rounded = popup.value.format(1).toDouble()
            val date = dates.getOrNull(popup.dataIndex) ?: ""
            "${rounded.format(1)}\n$date"
        }
    )

    ElevatedCard(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)) {
        BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
            var labels = DateUtils.generateAxisXLabels(
                startDate = startDate,
                endDate = endDate,
                locale = locale
            )
            val density = LocalDensity.current
            val screenWidthPx = with(density) { maxWidth.toPx() }
            val totalWidth = totalTextWidth(labels, textStyle)
            labels = DateUtils.reduceAxisXLabels(labels, totalWidth, screenWidthPx.toDouble())
            if (labels.size < 2) {
                labels = labels + labels
            }
            val labelProperties = LabelProperties(
                enabled = true,
                textStyle = textStyle,
                labels = labels,
                rotation = LabelProperties.Rotation(degree = 0f)
            )

            val totalBars = if (barGroups.isNotEmpty()) barGroups.first().values.size else 0
            val spacing = if (totalBars > 0 && (120 / totalBars) > 2) 2.dp else if (totalBars > 0) (120 / totalBars).dp else 0.dp
            val totalSpacing = if (totalBars > 0) spacing * (totalBars - 1) else 0.dp
            val barThickness = if (totalBars > 0) (maxWidth - (18 * 2).dp - totalSpacing) / totalBars else 0.dp
            val barProperties = BarProperties(
                thickness = barThickness,
                spacing = spacing,
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 8.dp, topLeft = 8.dp),
            )
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Column {
                    WeatherCardHeader(title = title, unit = unit, icon = icon)
                    Spacer(modifier = Modifier.height(16.dp))
                    ColumnChart(
                        modifier = Modifier
                            .heightIn(max = 300.dp),
                        data = barGroups,
                        barProperties = barProperties,
                        animationMode =
                        if (totalBars < 100) AnimationMode.Together(delayBuilder = { it * 10L })
                        else AnimationMode.Together(delayBuilder = { 0L }
                        ),
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