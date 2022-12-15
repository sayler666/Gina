package com.sayler666.gina.di

import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCaseImpl
import com.sayler666.gina.daysList.usecase.GetDaysUseCase
import com.sayler666.gina.daysList.usecase.GetDaysUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    @Singleton
    abstract fun provideGetDaysUseCase(useCase: GetDaysUseCaseImpl): GetDaysUseCase

    @Binds
    @Singleton
    abstract fun provideGetDayDetailsUseCase(useCase: GetDayDetailsUseCaseImpl): GetDayDetailsUseCase
}
