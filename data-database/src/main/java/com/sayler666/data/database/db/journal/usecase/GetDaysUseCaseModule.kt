package com.sayler666.data.database.db.journal.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GetDaysUseCaseModule {
    @Binds
    abstract fun provideGetDaysUseCase(useCase: GetDaysUseCaseImpl): GetDaysUseCase
}
