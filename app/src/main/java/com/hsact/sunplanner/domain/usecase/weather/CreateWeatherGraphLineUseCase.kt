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

class CreateWeatherGraphLineUseCase @Inject constructor() {
    operator fun invoke(
        label: String,
        values: List<Double>,
        dates: List<String>? = null,
        isDotsVisible: Boolean,
        isEdgesCurved: Boolean,
        color: Color,
        withTint: Boolean = true,
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
            firstGradientFillColor = if (withTint) color.copy(alpha = .5f) else Color.Transparent,
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = DrawStyle.Stroke(
                width = 2.dp
            ),
            dotProperties = DotProperties(
                enabled = isDotsVisible,
                //color = SolidColor(Color(0xFFFFFFFF)),
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