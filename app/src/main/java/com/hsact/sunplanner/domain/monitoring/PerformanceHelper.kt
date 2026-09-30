package com.hsact.sunplanner.domain.monitoring

interface PerformanceHelper {
    fun startTrace(name: String)
    fun stopTrace(name: String)
    fun putAttribute(traceName: String, attribute: String, value: String)
    fun putMetric(traceName: String, metricName: String, value: Long)
}
