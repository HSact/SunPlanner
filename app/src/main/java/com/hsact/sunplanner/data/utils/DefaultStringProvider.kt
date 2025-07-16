package com.hsact.sunplanner.data.utils

import android.content.Context
import com.hsact.sunplanner.R
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
}