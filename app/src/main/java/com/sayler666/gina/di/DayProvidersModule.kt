package com.sayler666.gina.di

import com.sayler666.gina.addDay.usecase.DayQuoteProvider
import com.sayler666.gina.addDay.usecase.ReminderDismissUseCase
import com.sayler666.gina.quotes.QuotesRepository
import com.sayler666.gina.reminder.usecase.ReminderDismissUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DayProvidersModule {
    @Binds
    abstract fun provideDayQuoteProvider(repo: QuotesRepository): DayQuoteProvider

    @Binds
    abstract fun provideReminderDismissUseCase(impl: ReminderDismissUseCaseImpl): ReminderDismissUseCase
}
