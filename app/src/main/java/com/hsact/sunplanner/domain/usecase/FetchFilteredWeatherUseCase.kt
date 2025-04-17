package com.hsact.sunplanner.domain.usecase

import com.hsact.sunplanner.data.WeatherRepository
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.data.utils.WeatherUtils
import com.hsact.sunplanner.data.network.WeatherRequestParams
import java.time.LocalDate

class FetchFilteredWeatherUseCase(private val repository: WeatherRepository) {
    suspend fun execute(
        params: WeatherRequestParams,
        startLD: LocalDate,
        endLD: LocalDate
    ): WeatherResponse {
        val response = repository.getWeather(
            latitude = params.latitude,
            longitude = params.longitude,
            startDate = params.startDate,
            endDate = params.endDate
        )
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