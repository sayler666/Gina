package com.sayler666.gina.day.di


import com.sayler666.gina.day.addDay.usecase.AddDayUseCase
import com.sayler666.gina.day.addDay.usecase.AddDayUseCaseImpl
import com.sayler666.gina.day.attachments.usecase.GetAttachmentIdsBySourceUseCase
import com.sayler666.gina.day.attachments.usecase.GetAttachmentIdsBySourceUseCaseImpl
import com.sayler666.gina.day.attachments.usecase.GetAttachmentWithDayUseCase
import com.sayler666.gina.day.attachments.usecase.GetAttachmentWithDayUseCaseImpl
import com.sayler666.gina.day.dayDetails.usecase.GetDayDetailsUseCase
import com.sayler666.gina.day.dayDetails.usecase.GetDayDetailsUseCaseImpl
import com.sayler666.gina.day.dayDetails.usecase.GetNextPreviousIdDayUseCase
import com.sayler666.gina.day.dayDetails.usecase.GetNextPreviousIdDayUseCaseUseCaseImpl
import com.sayler666.gina.day.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.day.dayDetailsEdit.usecase.DeleteDayUseCaseImpl
import com.sayler666.gina.day.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.day.dayDetailsEdit.usecase.EditDayUseCaseImpl
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayEditingViewModelSlice
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayEditingViewModelSliceImpl
import com.sayler666.gina.day.workinCopy.WorkingCopyStorage
import com.sayler666.gina.day.workinCopy.WorkingCopyStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DayModule {
    @Binds
    abstract fun provideGetDayDetailsUseCase(useCase: GetDayDetailsUseCaseImpl): GetDayDetailsUseCase

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
    abstract fun provideGetAttachmentIdsBySourceUseCase(useCase: GetAttachmentIdsBySourceUseCaseImpl): GetAttachmentIdsBySourceUseCase

    @Binds
    abstract fun getWorkingCopyStorage(storage: WorkingCopyStorageImpl): WorkingCopyStorage

    @Binds
    abstract fun provideDayEditingViewModelSlice(slice: DayEditingViewModelSliceImpl): DayEditingViewModelSlice
}
