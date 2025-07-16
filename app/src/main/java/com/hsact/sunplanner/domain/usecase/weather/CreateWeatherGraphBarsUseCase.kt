package com.hsact.sunplanner.domain.usecase.weather

import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import ir.ehsannarmani.compose_charts.models.Bars
import javax.inject.Inject
import kotlin.math.ceil

/**
 * Use case for creating bar chart data from weather values.
 *
 * Prepares a [Bars] object suitable for displaying weather data in a bar chart,
 * automatically reducing the number of data points if the list is too large.
 *
 * @property maxSize Maximum number of bars to display without reduction.
 */
class CreateWeatherGraphBarsUseCase @Inject constructor() {
    private val maxSize = 500

    /**
     * Creates a [Bars] instance with the given label, values, and color.
     *
     * If the number of values exceeds [maxSize], the values are averaged in chunks to reduce the size.
     *
     * @param label The label for the bars group.
     * @param values The list of double values representing the data points.
     * @param color The color used for the bars.
     * @return A [Bars] object representing the data ready for display.
     */
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

    /**
     * Reduces the size of the values list by averaging chunks of the original list.
     *
     * @param values The original list of values.
     * @return A reduced list of averaged values.
     */
    private fun reduceValues(values: List<Double>): List<Double> {
        val targetSize = maxSize
        val chunkSize = ceil(values.size / targetSize.toDouble()).toInt()

        return values.chunked(chunkSize).map { chunk ->
            chunk.average()
        }
    }
}