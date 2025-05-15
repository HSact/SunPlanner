package com.hsact.sunplanner.data.utils

interface StringProvider {
    fun locationEmpty(): String
    fun invalidYearRange(): String
    fun invalidDateRange(): String
    fun yearsRangeTooBig(limit: Int): String
    fun fetchCitiesError(e: Exception): String
    fun fetchWeatherError(e: Exception): String
    fun max(): String
    fun avg(): String
    fun min(): String
    fun sunshine(): String
    fun daylight(): String
    fun windSpeed(): String
    fun wind(): String
    fun gusts(): String
}