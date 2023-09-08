package com.sayler666.gina.di

import com.sayler666.gina.addDay.usecase.AddDayUseCase
import com.sayler666.gina.addDay.usecase.AddDayUseCaseImpl
import com.sayler666.gina.attachments.usecase.GetAttachmentWithDayUseCase
import com.sayler666.gina.attachments.usecase.GetAttachmentWithDayUseCaseImpl
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCaseImpl
import com.sayler666.gina.dayDetails.usecaase.GetNextPreviousDayUseCase
import com.sayler666.gina.dayDetails.usecaase.GetNextPreviousDayUseCaseUseCaseImpl
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCaseImpl
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCaseImpl
import com.sayler666.gina.dayDetailsEdit.usecase.GetFriendUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.GetFriendUseCaseImpl
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.AddFriendUseCaseImpl
import com.sayler666.gina.friends.usecase.DeleteFriendUseCase
import com.sayler666.gina.friends.usecase.DeleteFriendUseCaseImpl
import com.sayler666.gina.friends.usecase.EditFriendUseCase
import com.sayler666.gina.friends.usecase.EditFriendUseCaseImpl
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCaseImpl
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepository
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepositoryImpl
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import com.sayler666.gina.journal.usecase.GetDaysUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun provideGetDaysUseCase(useCase: GetDaysUseCaseImpl): GetDaysUseCase

    @Binds
    abstract fun provideGetDayDetailsUseCase(useCase: GetDayDetailsUseCaseImpl): GetDayDetailsUseCase

    @Binds
    abstract fun provideImageAttachmentsRepository(useCase: ImageAttachmentsRepositoryImpl): ImageAttachmentsRepository

    @Binds
    abstract fun provideGetNextPreviousDayDetailsUseCase(useCase: GetNextPreviousDayUseCaseUseCaseImpl): GetNextPreviousDayUseCase

    @Binds
    abstract fun provideEditDayUseCase(useCase: EditDayUseCaseImpl): EditDayUseCase

    @Binds
    abstract fun provideDeleteDayUseCase(useCase: DeleteDayUseCaseImpl): DeleteDayUseCase

    @Binds
    abstract fun provideAddDayUseCase(useCase: AddDayUseCaseImpl): AddDayUseCase

    @Binds
    abstract fun getFriendUseCase(useCase: GetFriendUseCaseImpl): GetFriendUseCase

    @Binds
    abstract fun getAllFriendsUseCase(useCase: GetAllFriendsUseCaseImpl): GetAllFriendsUseCase

    @Binds
    abstract fun addFriendUseCase(useCase: AddFriendUseCaseImpl): AddFriendUseCase

    @Binds
    abstract fun editFriendUseCase(useCase: EditFriendUseCaseImpl): EditFriendUseCase

    @Binds
    abstract fun deleteFriendUseCase(useCase: DeleteFriendUseCaseImpl): DeleteFriendUseCase

    @Binds
    abstract fun provideGetAttachmentWithDayUseCase(useCase: GetAttachmentWithDayUseCaseImpl): GetAttachmentWithDayUseCase

}
