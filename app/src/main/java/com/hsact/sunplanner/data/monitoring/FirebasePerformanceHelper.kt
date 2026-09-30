package com.hsact.sunplanner.data.monitoring

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.hsact.sunplanner.domain.monitoring.PerformanceHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePerformanceHelper @Inject constructor() : PerformanceHelper {
    private val traces = mutableMapOf<String, Trace>()

    override fun startTrace(name: String) {
        val trace = FirebasePerformance.getInstance().newTrace(name)
        trace.start()
        traces[name] = trace
    }

    override fun stopTrace(name: String) {
        traces[name]?.stop()
        traces.remove(name)
    }

    override fun putAttribute(traceName: String, attribute: String, value: String) {
        traces[traceName]?.putAttribute(attribute, value)
    }

    override fun putMetric(traceName: String, metricName: String, value: Long) {
        traces[traceName]?.putMetric(metricName, value)
    }
}
