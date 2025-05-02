package com.hsact.sunplanner.domain.usecase.weather

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import javax.inject.Inject

class CreateWeatherGraphLineUseCase @Inject constructor() {
    operator fun invoke(
        label: String,
        values: List<Double>,
        color: Color,
        isOneYear: Boolean = false
    ): Line {
        return Line(
            label = label,
            values = if (!isOneYear) values else values + values,
            color = SolidColor(color),
            firstGradientFillColor = color.copy(alpha = .5f),
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = DrawStyle.Stroke(
                width = 2.dp
            )
        )
    }
}