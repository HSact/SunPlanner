package com.hsact.sunplanner.di

import com.hsact.sunplanner.data.monitoring.FirebaseCrashReportingHelper
import com.hsact.sunplanner.data.monitoring.FirebasePerformanceHelper
import com.hsact.sunplanner.domain.monitoring.CrashReportingHelper
import com.hsact.sunplanner.domain.monitoring.PerformanceHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringModule {

    @Binds
    @Singleton
    abstract fun bindCrashReportingHelper(
        firebaseCrashReportingHelper: FirebaseCrashReportingHelper
    ): CrashReportingHelper

    @Binds
    @Singleton
    abstract fun bindPerformanceHelper(
        firebasePerformanceHelper: FirebasePerformanceHelper
    ): PerformanceHelper
}
