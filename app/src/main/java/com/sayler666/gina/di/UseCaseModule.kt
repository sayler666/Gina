package com.sayler666.gina.di

import com.sayler666.gina.journal.usecase.PreviousYearsAttachmentsUseCase
import com.sayler666.gina.journal.usecase.PreviousYearsAttachmentsUseCaseImpl
import com.sayler666.gina.reminder.usecase.NotificationUseCase
import com.sayler666.gina.reminder.usecase.NotificationUseCaseImpl
import com.sayler666.gina.reminder.usecase.TodayEntryExistUseCase
import com.sayler666.gina.reminder.usecase.TodayEntryExistUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun getTodayEntryExists(useCase: TodayEntryExistUseCaseImpl): TodayEntryExistUseCase

    @Binds
    abstract fun getPreviousYearsAttachmentsUseCase(useCase: PreviousYearsAttachmentsUseCaseImpl): PreviousYearsAttachmentsUseCase

    @Binds
    abstract fun getNotificationHelper(useCase: NotificationUseCaseImpl): NotificationUseCase
}
