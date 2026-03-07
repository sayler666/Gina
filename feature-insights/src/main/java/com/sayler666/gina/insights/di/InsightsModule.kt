package com.sayler666.gina.insights.di

import com.sayler666.gina.insights.usecase.GetAvgMoodByMonthsUseCase
import com.sayler666.gina.insights.usecase.GetAvgMoodByMonthsUseCaseImpl
import com.sayler666.gina.insights.usecase.GetAvgMoodByWeeksUseCase
import com.sayler666.gina.insights.usecase.GetAvgMoodByWeeksUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class InsightsModule {

    @Binds
    abstract fun getAvgMoodByMonthsUseCase(useCase: GetAvgMoodByMonthsUseCaseImpl): GetAvgMoodByMonthsUseCase

    @Binds
    abstract fun getAvgMoodByWeeksUseCase(useCase: GetAvgMoodByWeeksUseCaseImpl): GetAvgMoodByWeeksUseCase
}
