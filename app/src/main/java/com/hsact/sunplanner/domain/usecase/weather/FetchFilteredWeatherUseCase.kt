package com.hsact.sunplanner.domain.usecase.weather

import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.data.utils.WeatherUtils
import com.hsact.sunplanner.domain.repository.WeatherRepository
import java.time.LocalDate
import javax.inject.Inject

class FetchFilteredWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository) {
    suspend fun execute(
        params: WeatherRequestParams,
        startLD: LocalDate,
        endLD: LocalDate
    ): WeatherResponse {
        val response = repository.getWeather(params)
        val filtered = WeatherUtils.filterDailyWeatherByDateRange(
            response.daily, startLD, endLD
        )
        return if (filtered != null) {
            response.copy(daily = filtered)
        } else {
            response
        }
    }
}