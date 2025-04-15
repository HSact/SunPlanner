package com.hsact.sunplanner.domain.usecase

import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import ir.ehsannarmani.compose_charts.models.Bars

class CreateWeatherGraphBarsUseCase {
    operator fun invoke(
        label: String,
        values: List<Double>,
        color: Color
    ): Bars {
        val brush = SolidColor(color)

        val dataList = values.map { value ->
            Bars.Data(
                value = value,
                color = brush,
                animationSpec = tween(durationMillis = 1000),
                /*properties = BarProperties(
                    width = 16.dp,
                    cornerRadius = 4.dp
                )*/
            )
        }

        return Bars(
            label = label,
            values = dataList
        )
    }
}