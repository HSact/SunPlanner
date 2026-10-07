package com.hsact.sunplanner.domain.usecase.weather

import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.data.utils.WeatherUtils
import com.hsact.sunplanner.di.DefaultDispatcher
import com.hsact.sunplanner.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class FetchFilteredWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend fun execute(
        params: WeatherRequestParams,
        startLD: LocalDate,
        endLD: LocalDate
    ): WeatherResponse = withContext(defaultDispatcher) {
        val response = repository.getWeather(params)
        val filtered = WeatherUtils.filterDailyWeatherByDateRange(
            response.daily, startLD, endLD
        )
        if (filtered != null) {
            response.copy(daily = filtered)
        } else {
            response
        }
    }
}