package com.hsact.sunplanner.data.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.hsact.sunplanner.domain.analytics.AnalyticsHelper
import javax.inject.Inject

class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsHelper {
    companion object {
        private const val EVENT_APP_STARTED = "app_started"
        private const val EVENT_WEATHER_SEARCH_CLICKED = "weather_search_clicked"
        private const val EVENT_WEATHER_FETCHED = "weather_fetched"
        private const val EVENT_WEATHER_FETCH_FAILED = "weather_fetch_failed"

        private const val PARAM_LOCATION = "location"
        private const val PARAM_START_DATE = "start_date"
        private const val PARAM_END_DATE = "end_date"
        private const val PARAM_ERROR = "error"
    }

    override fun logAppStarted() {
        firebaseAnalytics.logEvent(EVENT_APP_STARTED) {}
    }

    override fun logWeatherSearchClicked(location: String, startDate: String, endDate: String) {
        firebaseAnalytics.logEvent(EVENT_WEATHER_SEARCH_CLICKED) {
            param(PARAM_LOCATION, location)
            param(PARAM_START_DATE, startDate)
            param(PARAM_END_DATE, endDate)
        }
    }

    override fun logWeatherFetched(location: String) {
        firebaseAnalytics.logEvent(EVENT_WEATHER_FETCHED) {
            param(PARAM_LOCATION, location)
        }
    }

    override fun logWeatherFetchFailed(location: String, error: String) {
        firebaseAnalytics.logEvent(EVENT_WEATHER_FETCH_FAILED) {
            param(PARAM_LOCATION, location)
            param(PARAM_ERROR, error)
        }
    }
}