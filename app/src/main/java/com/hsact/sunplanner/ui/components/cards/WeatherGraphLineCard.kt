package com.hsact.sunplanner.ui.components.cards

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.domain.model.ThemeMode
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import java.time.LocalDate
import java.util.Locale

@Composable
fun WeatherGraphLineCard(
    title: String,
    lineList: List<Line>,
    startDate: LocalDate,
    endDate: LocalDate,
    locale: Locale,
    icon: ImageVector? = null,
    unit: String? = null,
    theme: ThemeMode = ThemeMode.SYSTEM,
    minIsZero: Boolean = false
) {
    val (min, max) = remember(lineList) {
        val allValues = lineList.flatMap { it.values }
        val max = allValues.maxOrNull() ?: 0.0
        val min = if (minIsZero) 0.0 else allValues.minOrNull() ?: 0.0
        min to max
    }

    val hasAnyLabel = remember(lineList) {
        lineList.any { it.label?.isNotBlank() == true }
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

    val animationMode = if (lineList.isNotEmpty() && lineList.first().values.size < 100) {
        AnimationMode.Together(delayBuilder = { it * 500L })
    } else {
        AnimationMode.Together(delayBuilder = { 0L })
    }

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
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                Column {
                    WeatherCardHeader(title = title, unit = unit, icon = icon)
                    Spacer(modifier = Modifier.height(16.dp))
                    LineChart(
                        data = lineList,
                        animationMode = animationMode,
                        gridProperties = gridProperties,
                        zeroLineProperties = ZeroLineProperties(enabled = false),
                        indicatorProperties = indicatorProperties,
                        labelHelperProperties = labelHelperProperties,
                        labelProperties = labelProperties,
                        minValue = min,
                        maxValue = max,
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCardHeader(
    modifier: Modifier = Modifier,
    title: String,
    unit: String? = null,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (unit != null) {
            Text(
                text = " ($unit)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignByBaseline()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherGraphLineCardPreview() {
    val previewLine = Line(
        label = "Max",
        values = listOf(0.0, 2.0, -3.0, 7.0, 10.0, 12.0, 18.0, 25.0, 27.0, 30.0),
        color = SolidColor(Color(0xFFFF0000)),
        firstGradientFillColor = Color(0xFFFF0000).copy(alpha = .5f),
        secondGradientFillColor = Color.Transparent,
        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
        gradientAnimationDelay = 1000,
        drawStyle = DrawStyle.Stroke(width = 2.dp)
    )
        WeatherGraphLineCard(
            title = "Temperature",
            lineList = listOf(previewLine),
            startDate = LocalDate.now().minusDays(14),
            endDate = LocalDate.now(),
            locale = LocalLocale.current.platformLocale
        )
}