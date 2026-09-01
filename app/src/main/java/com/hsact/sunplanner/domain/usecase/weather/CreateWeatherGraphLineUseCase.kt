package com.hsact.sunplanner.domain.usecase.weather

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import javax.inject.Inject

/**
 * Use case for creating a line chart representation of weather data.
 */
class CreateWeatherGraphLineUseCase @Inject constructor() {

    /**
     * Creates a [Line] chart data object with the specified parameters.
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
            curvedEdges = isEdgesCurved
        )
    }
}
