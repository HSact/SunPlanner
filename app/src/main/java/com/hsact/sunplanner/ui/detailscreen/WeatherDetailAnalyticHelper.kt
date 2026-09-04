package com.hsact.sunplanner.ui.detailscreen

import com.hsact.sunplanner.domain.model.DetailedYearlyData
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.WeatherMetricType
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode
import com.hsact.sunplanner.domain.repository.StringProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

/**
 * Helper class for the Detail Screen that calculates statistical summaries and generates
 * qualitative insights (tips) based on historical weather data.
 */
class WeatherDetailAnalyticHelper @Inject constructor(
    private val stringProvider: StringProvider
) {
    /**
     * Generates a list of textual insights for a single location.
     * 
     * Insights include:
     * - Best 3-day window for the selected metric.
     * - Trends (warming/cooling).
     * - Probability of events (rain).
     * - Unusual conditions (exceptional sunshine, strong winds).
     *
     * @param data List of yearly historical data blocks.
     * @param type The type of weather metric being analyzed.
     * @param settings User settings for units and preferences.
     * @param locale User's current locale for date formatting.
     * @return List of strings representing human-readable tips.
     */
    fun generateInsights(
        data: List<DetailedYearlyData>,
        type: WeatherMetricType,
        settings: SettingsBundle,
        locale: Locale
    ): List<String> {
        if (data.isEmpty()) return emptyList()
        val insights = mutableListOf<String>()

        findBestWindow(data, type, locale)?.let { insights.add(it) }

        when (type) {
            WeatherMetricType.TEMPERATURE -> {
                val years = data.sortedBy { it.year }
                if (years.size >= 3) {
                    val firstAvg = years.first().metrics.averageTemps.average()
                    val lastAvg = years.last().metrics.averageTemps.average()
                    if (lastAvg > firstAvg + 1.0) insights.add(stringProvider.insightWarming())
                    else if (lastAvg < firstAvg - 1.0) insights.add(stringProvider.insightCooling())
                }
            }

            WeatherMetricType.PRECIPITATION -> {
                val allRainDays = data.flatMap { it.metrics.precipitation }.count { it > 0.5 }
                val totalDays = data.flatMap { it.metrics.precipitation }.size
                if (totalDays > 0) insights.add(stringProvider.insightRainProb((allRainDays.toDouble() / totalDays * 100).toInt()))
            }

            WeatherMetricType.SUNSHINE -> {
                val avgSunshine = data.flatMap { it.metrics.sunshine }.average()
                if (avgSunshine > 8.0) insights.add(stringProvider.insightSunnyPeriod())
            }

            WeatherMetricType.WIND -> {
                val maxWind = data.flatMap { it.metrics.windSpeed }.maxOrNull() ?: 0.0
                if (maxWind > 25.0) {
                    val unit = when (settings.windUnitMode) {
                        WindSpeedUnitMode.KMH -> "km/h"
                        WindSpeedUnitMode.MS -> "m/s"
                        WindSpeedUnitMode.MPH -> "mph"
                        WindSpeedUnitMode.KN -> "kn"
                    }
                    insights.add(stringProvider.insightStrongWind(maxWind, unit))
                }
            }

            WeatherMetricType.AIR_QUALITY -> {
                val avgAqi = data.flatMap { it.metrics.airQuality }.average()
                if (avgAqi < 20.0) insights.add(stringProvider.insightAqiExcellent())
                else if (avgAqi > 50.0) insights.add(stringProvider.insightAqiModerate())
            }
        }
        return insights
    }

    /**
     * Generates a comparative list of insights comparing two locations.
     */
    fun generateComparisonInsights(
        main: List<DetailedYearlyData>,
        comp: List<DetailedYearlyData>,
        type: WeatherMetricType,
        mainName: String,
        compName: String
    ): List<String> {
        val insights = mutableListOf<String>()
        when (type) {
            WeatherMetricType.TEMPERATURE -> {
                val mainAvg = main.flatMap { it.metrics.averageTemps }.average()
                val compAvg = comp.flatMap { it.metrics.averageTemps }.average()
                val diff = mainAvg - compAvg
                val winner = if (diff > 0) mainName else compName
                val loser = if (diff > 0) compName else mainName
                insights.add(stringProvider.insightCompTemp(winner, loser, abs(diff)))
            }

            WeatherMetricType.SUNSHINE -> {
                val mainAvg = main.flatMap { it.metrics.sunshine }.average()
                val compAvg = comp.flatMap { it.metrics.sunshine }.average()
                val diff = mainAvg - compAvg
                val winner = if (diff > 0) mainName else compName
                insights.add(stringProvider.insightCompSun(winner, abs(diff)))
            }

            WeatherMetricType.PRECIPITATION -> {
                val mainSum = main.flatMap { it.metrics.precipitation }.sum()
                val compSum = comp.flatMap { it.metrics.precipitation }.sum()
                val wetter = if (mainSum > compSum) mainName else compName
                insights.add(stringProvider.insightCompRain(wetter))
            }

            WeatherMetricType.AIR_QUALITY -> {
                val mainAvg = main.flatMap { it.metrics.airQuality }.average()
                val compAvg = comp.flatMap { it.metrics.airQuality }.average()
                val cleaner = if (mainAvg < compAvg) mainName else compName
                insights.add(stringProvider.insightCompAqi(cleaner))
            }

            else -> {}
        }
        return insights
    }

    /**
     * Algorithmically finds the most "attractive" 3-day window for a given metric.
     * For example, for temperature, it looks for the warmest window with least variance.
     */
    private fun findBestWindow(
        data: List<DetailedYearlyData>,
        type: WeatherMetricType,
        locale: Locale
    ): String? {
        if (data.isEmpty() || data.first().metrics.averageTemps.size < 4) return null

        val numDays = data.first().dateLabels.size
        val avgMetrics = (0 until numDays).map { dayIdx ->
            when (type) {
                WeatherMetricType.TEMPERATURE -> data.map { it.metrics.averageTemps[dayIdx] }
                    .average()

                WeatherMetricType.PRECIPITATION -> data.map { it.metrics.precipitation[dayIdx] }
                    .average()

                WeatherMetricType.SUNSHINE -> data.map { it.metrics.sunshine[dayIdx] }.average()
                WeatherMetricType.WIND -> data.map { it.metrics.windSpeed[dayIdx] }.average()
                WeatherMetricType.AIR_QUALITY -> data.map { it.metrics.airQuality[dayIdx] }
                    .average()
            }
        }

        val windowSize = 3
        var bestIndex = 0
        var bestScore = -Double.MAX_VALUE

        for (i in 0..avgMetrics.size - windowSize) {
            val window = avgMetrics.subList(i, i + windowSize)
            val score = when (type) {
                WeatherMetricType.TEMPERATURE -> window.average() - (window.maxOrNull()!! - window.minOrNull()!!)
                WeatherMetricType.PRECIPITATION -> -window.sum()
                WeatherMetricType.SUNSHINE -> window.sum()
                WeatherMetricType.WIND -> -window.sum()
                WeatherMetricType.AIR_QUALITY -> -window.sum()
            }
            if (score > bestScore) {
                bestScore = score
                bestIndex = i
            }
        }

        val pattern = if (locale.language == "ru") "d MMMM" else "MMM d"
        val fmt = DateTimeFormatter.ofPattern(pattern, locale)
        val start = LocalDate.parse(data.first().dateLabels[bestIndex]).format(fmt)
        val end = LocalDate.parse(data.first().dateLabels[bestIndex + windowSize - 1]).format(fmt)

        val metricName = when (type) {
            WeatherMetricType.TEMPERATURE -> stringProvider.metricTemperatureBest()
            WeatherMetricType.PRECIPITATION -> stringProvider.metricDryWeatherBest()
            WeatherMetricType.SUNSHINE -> stringProvider.metricSunshineBest()
            WeatherMetricType.WIND -> stringProvider.metricWindBest()
            WeatherMetricType.AIR_QUALITY -> stringProvider.metricAqiBest()
        }

        return stringProvider.insightBestWindow(metricName, start, end)
    }

    /**
     * Calculates the statistical summary (Max, Min, Avg) for a given set of yearly data.
     */
    fun calculateSummary(
        data: List<DetailedYearlyData>,
        type: WeatherMetricType
    ): WeatherDetailSummary {
        if (data.isEmpty()) return WeatherDetailSummary()
        return when (type) {
            WeatherMetricType.TEMPERATURE -> {
                val allMax = data.flatMap { it.metrics.maxTemps }
                val allMin = data.flatMap { it.metrics.minTemps }
                WeatherDetailSummary(
                    allMax.maxOrNull() ?: 0.0,
                    allMin.minOrNull() ?: 0.0,
                    (allMax.average() + allMin.average()) / 2.0
                )
            }

            WeatherMetricType.SUNSHINE -> {
                val all = data.flatMap { it.metrics.sunshine }
                WeatherDetailSummary(all.maxOfOrNull { it } ?: 0.0,
                    all.minOfOrNull { it } ?: 0.0,
                    all.average())
            }

            WeatherMetricType.PRECIPITATION -> {
                val all = data.flatMap { it.metrics.precipitation }
                WeatherDetailSummary(all.maxOfOrNull { it } ?: 0.0,
                    all.minOfOrNull { it } ?: 0.0,
                    all.average())
            }

            WeatherMetricType.WIND -> {
                val all = data.flatMap { it.metrics.windSpeed }
                WeatherDetailSummary(all.maxOfOrNull { it } ?: 0.0,
                    all.minOfOrNull { it } ?: 0.0,
                    all.average())
            }

            WeatherMetricType.AIR_QUALITY -> {
                val all = data.flatMap { it.metrics.airQuality }
                WeatherDetailSummary(all.maxOfOrNull { it } ?: 0.0,
                    all.minOfOrNull { it } ?: 0.0,
                    all.average())
            }
        }
    }
}
