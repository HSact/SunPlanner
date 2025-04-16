package com.hsact.sunplanner.domain.usecase

import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import ir.ehsannarmani.compose_charts.models.Bars
import java.time.LocalDate

class CreateWeatherGraphBarsUseCase {
    operator fun invoke(
        label: String,
        values: List<Double>,
        startDate: LocalDate,
        endDate: LocalDate,
        color: Color
    ): Bars {
        val brush = SolidColor(color)
        val useYearsAsLabels = //TODO: Clean up
            startDate.dayOfMonth == endDate.dayOfMonth &&
                startDate.month == endDate.month

        val dataList = values.mapIndexed { index, value ->
            /*val labelText = if (useYearsAsLabels) {
                (startDate.year + index).toString()
            } else {
                startDate.plusDays(index.toLong()).dayOfMonth.toString()
            }*/
            Bars.Data(
                //label = labelText,
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