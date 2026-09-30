package com.hsact.sunplanner.domain.monitoring

interface CrashReportingHelper {
    fun log(message: String)
    fun recordException(throwable: Throwable)
    fun setCustomKey(key: String, value: String)
    fun setCustomKey(key: String, value: Int)
    fun setCustomKey(key: String, value: Boolean)
}
