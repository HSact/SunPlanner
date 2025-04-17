package com.hsact.sunplanner.domain.usecase

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.models.Line

class CreateWeatherGraphLineUseCase {
    operator fun invoke(
        label: String,
        values: List<Double>,
        color: Color
    ): Line {
        return Line(
            label = label,
            values = values,
            color = SolidColor(color),
            firstGradientFillColor = color.copy(alpha = .5f),
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = ir.ehsannarmani.compose_charts.models.DrawStyle.Stroke(
                width = 2.dp
            )
        )
    }
}