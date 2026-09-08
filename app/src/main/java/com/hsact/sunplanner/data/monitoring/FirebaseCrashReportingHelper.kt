package com.hsact.sunplanner.data.monitoring

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hsact.sunplanner.domain.monitoring.CrashReportingHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCrashReportingHelper @Inject constructor() : CrashReportingHelper {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }
}
