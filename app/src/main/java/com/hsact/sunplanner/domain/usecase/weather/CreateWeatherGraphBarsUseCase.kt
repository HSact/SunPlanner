package com.hsact.sunplanner.domain.usecase.weather

import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import ir.ehsannarmani.compose_charts.models.Bars
import javax.inject.Inject
import kotlin.math.ceil

class CreateWeatherGraphBarsUseCase @Inject constructor() {
    private val maxSize = 500
    operator fun invoke(
        label: String,
        values: List<Double>,
        color: Color
    ): Bars {
        val brush = SolidColor(color)
        val reducedValues = if (values.size > maxSize) {
            reduceValues(values)
        } else {
            values
        }
        val dataList = reducedValues.map { avg ->
            Bars.Data(
                value = avg,
                color = brush,
                animationSpec = tween(durationMillis = 1000)
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

    private fun reduceValues(values: List<Double>): List<Double> {
        val targetSize = maxSize
        val chunkSize = ceil(values.size / targetSize.toDouble()).toInt()

        return values.chunked(chunkSize).map { chunk ->
            chunk.average()
        }
    }
}