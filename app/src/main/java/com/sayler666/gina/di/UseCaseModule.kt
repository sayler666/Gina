package com.sayler666.gina.di

import com.sayler666.gina.addDay.usecase.AddDayUseCase
import com.sayler666.gina.addDay.usecase.AddDayUseCaseImpl
import com.sayler666.gina.attachments.usecase.GetAttachmentWithDayUseCase
import com.sayler666.gina.attachments.usecase.GetAttachmentWithDayUseCaseImpl
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCaseImpl
import com.sayler666.gina.dayDetails.usecaase.GetNextPreviousIdDayUseCase
import com.sayler666.gina.dayDetails.usecaase.GetNextPreviousIdDayUseCaseUseCaseImpl
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCaseImpl
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCaseImpl
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepository
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepositoryImpl
import com.sayler666.gina.journal.usecase.PreviousYearsAttachmentsUseCase
import com.sayler666.gina.journal.usecase.PreviousYearsAttachmentsUseCaseImpl
import com.sayler666.gina.reminder.usecase.NotificationUseCase
import com.sayler666.gina.reminder.usecase.NotificationUseCaseImpl
import com.sayler666.gina.reminder.usecase.TodayEntryExistUseCase
import com.sayler666.gina.reminder.usecase.TodayEntryExistUseCaseImpl
import com.sayler666.gina.workinCopy.WorkingCopyStorage
import com.sayler666.gina.workinCopy.WorkingCopyStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun provideGetDayDetailsUseCase(useCase: GetDayDetailsUseCaseImpl): GetDayDetailsUseCase

    @Binds
    abstract fun provideImageAttachmentsRepository(useCase: ImageAttachmentsRepositoryImpl): ImageAttachmentsRepository

    @Binds
    abstract fun provideGetNextPreviousDayDetailsUseCase(useCase: GetNextPreviousIdDayUseCaseUseCaseImpl): GetNextPreviousIdDayUseCase

    @Binds
    abstract fun provideEditDayUseCase(useCase: EditDayUseCaseImpl): EditDayUseCase

    @Binds
    abstract fun provideDeleteDayUseCase(useCase: DeleteDayUseCaseImpl): DeleteDayUseCase

    @Binds
    abstract fun provideAddDayUseCase(useCase: AddDayUseCaseImpl): AddDayUseCase

    @Binds
    abstract fun provideGetAttachmentWithDayUseCase(useCase: GetAttachmentWithDayUseCaseImpl): GetAttachmentWithDayUseCase

    @Binds
    abstract fun getTodayEntryExists(useCase: TodayEntryExistUseCaseImpl): TodayEntryExistUseCase

    @Binds
    abstract fun getPreviousYearsAttachmentsUseCase(useCase: PreviousYearsAttachmentsUseCaseImpl): PreviousYearsAttachmentsUseCase

    @Binds
    abstract fun getNotificationHelper(useCase: NotificationUseCaseImpl): NotificationUseCase

    @Binds
    abstract fun getWorkingCopyStorage(storage: WorkingCopyStorageImpl): WorkingCopyStorage
}
