package com.hsact.sunplanner.domain.repository

import com.hsact.sunplanner.domain.error.ApiError

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
    fun insightWarming(): String
    fun insightCooling(): String
    fun insightRainProb(prob: Int): String
    fun insightSunnyPeriod(): String
    fun insightStrongWind(value: Double, unit: String): String

    fun insightAqiExcellent(): String
    fun insightAqiModerate(): String

    fun insightCompTemp(winner: String, loser: String, diff: Double): String
    fun insightCompSun(winner: String, diff: Double): String
    fun insightCompRain(wetter: String): String
    fun insightCompAqi(cleaner: String): String
    fun insightBestWindow(metric: String, start: String, end: String): String

    fun metricTemperatureBest(): String
    fun metricSunshineBest(): String
    fun metricDryWeatherBest(): String
    fun metricWindBest(): String
    fun metricAqiBest(): String

    fun errorTooManyRequests(): String
    fun errorBadRequest(reason: String?): String
    fun errorServerError(): String
    fun errorInvalidResponse(): String
    fun errorNoConnection(): String
    fun errorUnknown(): String

    fun getApiErrorMessage(error: ApiError): String
}
