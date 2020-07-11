package com.sayler.gina3.di

import com.sayler.gina3.days.domain.DaysUseCase
import com.sayler.gina3.days.domain.DaysUseCaseImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Singleton
    @Provides
    fun provideMoshi() = Moshi.Builder().build()

    @Provides
    fun provideDaysUseCase(daysUseCaseImpl: DaysUseCaseImpl): DaysUseCase = daysUseCaseImpl
}