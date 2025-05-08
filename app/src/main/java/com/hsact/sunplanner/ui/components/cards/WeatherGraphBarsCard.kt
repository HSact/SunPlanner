package com.hsact.sunplanner.ui.components.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.*
import java.time.LocalDate

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun WeatherGraphBarsCard(
    header: String,
    barGroups: List<Bars>,
    dates: List<String>,
    startDate: LocalDate,
    endDate: LocalDate,
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
        if (isDarkTheme) TextStyle(color = Color.White)
        else TextStyle(color = Color.Black)
    }

    val labelProperties = LabelProperties(
        enabled = true,
        textStyle = textStyle,
        labels = DateUtils.generateAxisXLabels(startDate, endDate),
        rotation = LabelProperties.Rotation(degree = 0f)
    )

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
        contentBuilder = { _, dataIndex, value ->
            val rounded = value.format(1).toDouble()
            val date = dates.getOrNull(dataIndex)?: ""
            "${rounded.format(1)}\n$date"
        }
    )

    Card(modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 20.dp)) {
        BoxWithConstraints(modifier = Modifier.padding(10.dp)) {

            val totalBars = barGroups[0].values.size
            val spacing = if ((120 / totalBars).toInt() > 2) 2.dp else (120 / totalBars).toInt().dp
            val totalSpacing = spacing * (totalBars - 1)
            val barThickness = (maxWidth - (18 * 2).dp - totalSpacing) / totalBars
            val barProperties = BarProperties(
                thickness = barThickness,
                spacing = spacing,
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 5.dp, topLeft = 5.dp),
            )
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

                ColumnChart(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .padding(top = 50.dp),
                    data = barGroups,
                    barProperties = barProperties,
                    animationMode = AnimationMode.Together(
                        delayBuilder = { it * 10L }
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
        header = "Average Temperature",
        barGroups = listOf(previewBars),
        dates = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
        startDate = LocalDate.now().minusDays(10),
        endDate = LocalDate.now()
    )
}