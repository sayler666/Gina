package com.sayler666.gina.reminders.di

import com.sayler666.gina.reminders.usecase.NotificationUseCase
import com.sayler666.gina.reminders.usecase.NotificationUseCaseImpl
import com.sayler666.gina.reminders.usecase.ReminderDismissUseCase
import com.sayler666.gina.reminders.usecase.ReminderDismissUseCaseImpl
import com.sayler666.gina.reminders.usecase.TodayEntryExistUseCase
import com.sayler666.gina.reminders.usecase.TodayEntryExistUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemindersModule {

    @Binds
    @Singleton
    abstract fun bindReminderDismissUseCase(impl: ReminderDismissUseCaseImpl): ReminderDismissUseCase

    @Binds
    @Singleton
    abstract fun bindNotificationUseCase(impl: NotificationUseCaseImpl): NotificationUseCase

    @Binds
    @Singleton
    abstract fun bindTodayEntryExistUseCase(impl: TodayEntryExistUseCaseImpl): TodayEntryExistUseCase
}
