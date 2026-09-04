package com.hsact.sunplanner.data.utils

import android.content.Context
import com.hsact.sunplanner.R
import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.repository.StringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultStringProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) : StringProvider {

    override fun locationEmpty() = context.getString(R.string.error_location_empty)
    override fun invalidYearRange() = context.getString(R.string.error_invalid_year_range)
    override fun invalidDateRange() = context.getString(R.string.error_invalid_date_range)
    override fun yearsRangeTooBig(limit: Int) =
        context.getString(R.string.error_years_range_too_big, limit)
    override fun fetchCitiesError(e: Exception) =
        context.getString(R.string.error_fetching_cities, e.message ?: "unknown")
    override fun fetchWeatherError(e: Exception) =
        context.getString(R.string.error_fetching_weather, e.message ?: "unknown")

    override fun max() = context.getString(R.string.max)
    override fun avg() = context.getString(R.string.avg)
    override fun min() = context.getString(R.string.min)
    override fun sunshine() = context.getString(R.string.sunshine)
    override fun daylight() = context.getString(R.string.daylight)
    override fun windSpeed() = context.getString(R.string.wind_speed)
    override fun wind() = context.getString(R.string.wind)
    override fun gusts() = context.getString(R.string.gusts)

    override fun insightWarming() = context.getString(R.string.insight_warming)
    override fun insightCooling() = context.getString(R.string.insight_cooling)
    override fun insightRainProb(prob: Int) = context.getString(R.string.insight_rain_prob, prob)
    override fun insightSunnyPeriod() = context.getString(R.string.insight_sunny_period)
    override fun insightStrongWind(value: Double, unit: String) =
        context.getString(R.string.insight_strong_wind, value, unit)

    override fun insightAqiExcellent() = context.getString(R.string.insight_aqi_excellent)
    override fun insightAqiModerate() = context.getString(R.string.insight_aqi_moderate)

    override fun insightCompTemp(winner: String, loser: String, diff: Double) =
        context.getString(R.string.insight_comp_temp, winner, loser, diff)

    override fun insightCompSun(winner: String, diff: Double) =
        context.getString(R.string.insight_comp_sun, winner, diff)

    override fun insightCompRain(wetter: String) =
        context.getString(R.string.insight_comp_rain, wetter)

    override fun insightCompAqi(cleaner: String) =
        context.getString(R.string.insight_comp_aqi, cleaner)

    override fun insightBestWindow(metric: String, start: String, end: String) =
        context.getString(R.string.insight_best_window, metric, start, end)

    override fun metricTemperatureBest() = context.getString(R.string.metric_temperature_best)
    override fun metricSunshineBest() = context.getString(R.string.metric_sunshine_best)
    override fun metricDryWeatherBest() = context.getString(R.string.metric_dry_weather_best)
    override fun metricWindBest() = context.getString(R.string.metric_wind_best)
    override fun metricAqiBest() = context.getString(R.string.metric_aqi_best)

    override fun errorTooManyRequests() = context.getString(R.string.error_too_many_requests)
    override fun errorBadRequest(reason: String?) =
        reason?.let { context.getString(R.string.error_bad_request_with_reason, it) }
            ?: context.getString(R.string.error_bad_request)

    override fun errorServerError() = context.getString(R.string.error_server_error)
    override fun errorInvalidResponse() = context.getString(R.string.error_invalid_response)
    override fun errorNoConnection() = context.getString(R.string.error_no_connection)
    override fun errorUnknown() = context.getString(R.string.error_unknown)

    override fun getApiErrorMessage(error: ApiError): String {
        return when (error) {
            ApiError.TooManyRequests -> errorTooManyRequests()
            is ApiError.BadRequest -> errorBadRequest(error.reason)
            ApiError.ServerError -> errorServerError()
            ApiError.InvalidResponse -> errorInvalidResponse()
            ApiError.NoConnection -> errorNoConnection()
            ApiError.EmptyResponse -> errorInvalidResponse()
            is ApiError.Unknown -> errorUnknown()
        }
    }
}
