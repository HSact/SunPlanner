package com.hsact.sunplanner.domain.analytics

interface AnalyticsHelper {
    fun logAppStarted()
    fun logWeatherSearchClicked(location: String, startDate: String, endDate: String)
    fun logWeatherFetched(location: String)
    fun logWeatherFetchFailed(location: String, error: String)
}