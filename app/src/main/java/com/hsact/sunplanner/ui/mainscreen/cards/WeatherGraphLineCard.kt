package com.hsact.sunplanner.ui.mainscreen.cards

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties

class WeatherGraphLineCard {
    @Composable
    fun WeatherCard(
        header: String,
        lineList: List<Line>
    ) {
        val allValues = lineList.flatMap { it.values }
        val max = allValues.maxOrNull() ?: 0.0
        val min = allValues.minOrNull() ?: 0.0

        val hasAnyLabel = lineList.any { it.label.isNotBlank() }

        val isDarkTheme = isSystemInDarkTheme()
        val textStyle = if (isDarkTheme) TextStyle(color = Color.White)
        else TextStyle(color = Color.Black)

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

        Card (modifier = Modifier.padding(top = 20.dp)) {
            Box(modifier = Modifier.padding(10.dp)) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    LineChart(
                        data = lineList,
                        animationMode = ir.ehsannarmani.compose_charts.models.AnimationMode.Together(
                            delayBuilder = { it * 500L }
                        ),
                        gridProperties = gridProperties,
                        zeroLineProperties = ZeroLineProperties(enabled = false),
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
        val previewLine = Line(
            label = "Max",
            values = listOf(0.0, 2.0, -3.0, 7.0, 10.0, 12.0, 18.0, 25.0, 27.0, 30.0),
            color = SolidColor(Color(0xFFFF0000)),
            firstGradientFillColor = Color(0xFFFF0000).copy(alpha = .5f),
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = ir.ehsannarmani.compose_charts.models.DrawStyle.Stroke(width = 2.dp)
        )
        WeatherCard(lineList = listOf(previewLine), header = "Temperature")
    }
}