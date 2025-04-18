package com.hsact.sunplanner.ui.mainscreen.cards

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
import com.hsact.sunplanner.data.utils.DateUtils
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.*
import java.time.LocalDate

class WeatherGraphBarsLineCard {
    @SuppressLint("UnusedBoxWithConstraintsScope")
    @Composable
    fun WeatherCard(
        header: String,
        barGroups: List<Bars>,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        val max = remember(barGroups) {
            val allValues = barGroups.flatMap { it.values.map { data -> data.value } }
            allValues.maxOrNull() ?: 0.0
        }

        val hasAnyLabel = remember(barGroups) {
            barGroups.any { it.label.isNotBlank() }
        }

        val isDarkTheme = isSystemInDarkTheme()
        val textStyle = remember(isDarkTheme) {
            if (isDarkTheme) TextStyle(color = Color.White)
            else TextStyle(color = Color.Black)
        }

        val labelProperties = LabelProperties(
            enabled = true,
            textStyle = textStyle,
            labels = DateUtils.labelsForCard(startDate, endDate),
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

        Card(modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 20.dp)) {
            BoxWithConstraints(modifier = Modifier.padding(10.dp)) {

                val totalBars = barGroups[0].values.size
                val spacing = 2.dp
                val totalSpacing = spacing * (totalBars - 1)
                val barThickness = (maxWidth - (15 * 2).dp - totalSpacing) / totalBars
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
                        data = barGroups,
                        barProperties = barProperties,
                        animationMode = AnimationMode.Together(
                            delayBuilder = { it * 100L }
                        ),
                        gridProperties = gridProperties,
                        indicatorProperties = indicatorProperties,
                        labelHelperProperties = labelHelperProperties,
                        labelProperties = labelProperties,
                        minValue = 0.0,
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
            barGroups = listOf(previewBars),
            startDate = LocalDate.now().minusDays(10),
            endDate = LocalDate.now()
        )
    }
}