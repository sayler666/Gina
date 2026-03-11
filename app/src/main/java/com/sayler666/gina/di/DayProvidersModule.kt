package com.sayler666.gina.di

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
    abstract fun provideDayQuoteProvider(repo: QuotesRepository): com.sayler666.gina.day.addDay.usecase.DayQuoteProvider

    @Binds
    abstract fun provideReminderDismissUseCase(impl: ReminderDismissUseCaseImpl): com.sayler666.gina.day.addDay.usecase.ReminderDismissUseCase
}
