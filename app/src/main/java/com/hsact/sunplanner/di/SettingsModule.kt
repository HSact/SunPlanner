package com.hsact.sunplanner.di

import com.hsact.sunplanner.data.repository.BookmarkRepositoryImpl
import com.hsact.sunplanner.data.repository.SettingsRepositoryImpl
import com.hsact.sunplanner.domain.repository.BookmarkRepository
import com.hsact.sunplanner.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        impl: BookmarkRepositoryImpl
    ): BookmarkRepository
}
