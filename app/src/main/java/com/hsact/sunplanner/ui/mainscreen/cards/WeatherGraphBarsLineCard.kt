package com.hsact.sunplanner.ui.mainscreen.cards

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.*

class WeatherGraphBarsLineCard {
    @Composable
    fun WeatherCard(
        header: String,
        barGroups: List<Bars>
    ) {
        val allValues = barGroups.flatMap { it.values.map { data -> data.value } }
        val max = allValues.maxOrNull() ?: 0.0
        val min = allValues.minOrNull() ?: 0.0

        val hasAnyLabel = barGroups.any { it.label.isNotBlank() }

        val isDarkTheme = isSystemInDarkTheme()
        val textStyle = if (isDarkTheme) TextStyle(color = Color.White) else TextStyle(color = Color.Black)

        val labelProperties = LabelProperties(
            enabled = true,
            textStyle = textStyle
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

        Card {
            Box(modifier = Modifier.padding(10.dp)) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    ColumnChart(
                        data = barGroups,
                        animationMode = AnimationMode.Together(
                            delayBuilder = { it * 500L }
                        ),
                        gridProperties = gridProperties,
                        indicatorProperties = indicatorProperties,
                        labelHelperProperties = labelHelperProperties,
                        labelProperties = labelProperties,
                        minValue = min,
                        maxValue = max,
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .padding(top = 50.dp)
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CardPreview() {
        val barData = listOf(
            Bars.Data(value = 4.0, color = SolidColor(Color(0xFF4CAF50))),
            Bars.Data(value = 7.0, color = SolidColor(Color(0xFF2196F3))),
            Bars.Data(value = 10.0, color = SolidColor(Color(0xFFFF9800))),
        )

        val previewBars = Bars(
            label = "Avg Temp",
            values = barData
        )

        WeatherCard(
            header = "Average Temperature",
            barGroups = listOf(previewBars)
        )
    }
}