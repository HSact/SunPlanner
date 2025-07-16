package com.hsact.sunplanner.domain.usecase.weather

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import javax.inject.Inject

/**
 * Use case for creating a line chart representation of weather data.
 *
 * Prepares a [Line] object for displaying weather values with optional dates,
 * customizable appearance, and animation effects.
 */
class CreateWeatherGraphLineUseCase @Inject constructor() {

    /**
     * Creates a [Line] chart data object with the specified parameters.
     *
     * If `isOneYear` is true and the values list contains less than 2 points,
     * duplicates the data to ensure the line renders properly.
     *
     * @param label The label for the line chart.
     * @param values The list of double values representing the data points.
     * @param dates Optional list of date strings corresponding to each value.
     * @param isDotsVisible Whether to show dots at each data point.
     * @param isEdgesCurved Whether the line edges should be curved.
     * @param color The color of the line.
     * @param tintOpacity Opacity for the gradient fill color (default 0.5).
     * @param isOneYear Indicates if the data represents one year, affecting rendering logic.
     * @return A configured [Line] object ready for rendering.
     */
    operator fun invoke(
        label: String,
        values: List<Double>,
        dates: List<String>? = null,
        isDotsVisible: Boolean,
        isEdgesCurved: Boolean,
        color: Color,
        tintOpacity: Float = 0.5F,
        isOneYear: Boolean = false
    ): Line {
        val fixedValues = if (isOneYear && values.size < 2) values + values else values
        val fixedDates = if (isOneYear && dates!!.size < 2) dates + dates else dates

        data class Point(val date: String, val value: Double)

        val points = fixedDates!!.zip(fixedValues) { date, value -> Point(date, value) }
        return Line(
            label = label,
            values = if (isOneYear && values.size < 2) values + values else values,
            color = SolidColor(color),
            firstGradientFillColor = color.copy(alpha = tintOpacity),
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = DrawStyle.Stroke(
                width = 2.dp
            ),
            dotProperties = DotProperties(
                enabled = isDotsVisible,
                color = SolidColor(color),
            ),
            popupProperties = if (!dates.isNullOrEmpty()) {
                PopupProperties(
                    textStyle = TextStyle.Default.copy(fontSize = 12.sp, color = Color.White),
                    contentBuilder = { _, dataIndex, value ->
                        val rounded = value.format(1).toDouble()
                        val date = points.getOrNull(dataIndex)?.date ?: ""
                        "${rounded.format(1)}\n$date"
                    }
                )
            } else null,
            curvedEdges = isEdgesCurved
        )
    }
}