package com.sayler666.gina.feature.journal.di

import com.sayler666.gina.feature.journal.usecase.PreviousYearsAttachmentsUseCase
import com.sayler666.gina.feature.journal.usecase.PreviousYearsAttachmentsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface JournalModule {
    @Binds
    fun bindPreviousYearsAttachmentsUseCase(
        impl: PreviousYearsAttachmentsUseCaseImpl
    ): PreviousYearsAttachmentsUseCase
}
