package com.hsact.sunplanner.domain.usecase.weather

import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.model.DetailedYearlyData
import com.hsact.sunplanner.domain.model.WeatherMetrics
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt

class GetDetailedYearlyDataUseCase @Inject constructor() {
    fun execute(data: WeatherResponse): List<DetailedYearlyData> {
        val daily = data.daily
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val groupedByYear = daily.time.indices.groupBy { index ->
            LocalDate.parse(daily.time[index], formatter).year
        }

        return groupedByYear.map { (year, indices) ->
            val metrics = WeatherMetrics(
                maxTemps = indices.map { daily.maxTemperature[it] },
                minTemps = indices.map { daily.minTemperature[it] },
                averageTemps = indices.map { (daily.maxTemperature[it] + daily.minTemperature[it]) / 2.0 },
                sunshine = indices.map { (daily.sunshineDuration[it] / 3600.0 * 10).roundToInt() / 10.0 },
                dayLight = indices.map { (daily.daylightDuration[it] / 3600.0 * 10).roundToInt() / 10.0 },
                precipitation = indices.map { daily.precipitationSum[it] },
                windSpeed = indices.map { daily.windSpeedMax[it] },
                gustSpeed = indices.map { daily.windGustsMax[it] },
                weatherCodes = indices.map { daily.code[it].toInt() },
                airQuality = indices.map { daily.european_aqi.getOrNull(it) ?: 0.0 }
            )
            val labels = indices.map { daily.time[it] }
            DetailedYearlyData(year, metrics, labels)
        }.sortedByDescending { it.year }
    }
}
