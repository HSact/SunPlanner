package com.hsact.sunplanner.data.utils

interface StringProvider {
    fun locationEmpty(): String
    fun invalidDateRange(): String
    fun yearsRangeTooBig(): String
    fun fetchCitiesError(e: Exception): String
    fun fetchWeatherError(e: Exception): String
    fun max(): String
    fun avg(): String
    fun min(): String
    fun windSpeed(): String
    fun wind(): String
    fun gusts(): String
}