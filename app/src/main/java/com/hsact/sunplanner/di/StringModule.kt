package com.hsact.sunplanner.di

import android.content.Context
import com.hsact.sunplanner.data.utils.DefaultStringProvider
import com.hsact.sunplanner.data.utils.StringProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object StringModule {
    @Provides
    fun provideStringProvider(
        @ApplicationContext context: Context
    ): StringProvider = DefaultStringProvider(context)
}