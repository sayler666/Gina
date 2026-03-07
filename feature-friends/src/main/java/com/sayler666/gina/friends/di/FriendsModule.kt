package com.sayler666.gina.friends.di

import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.AddFriendUseCaseImpl
import com.sayler666.gina.friends.usecase.DeleteFriendUseCase
import com.sayler666.gina.friends.usecase.DeleteFriendUseCaseImpl
import com.sayler666.gina.friends.usecase.EditFriendUseCase
import com.sayler666.gina.friends.usecase.EditFriendUseCaseImpl
import com.sayler666.gina.friends.usecase.GetAllFriendsByRecentUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsByRecentUseCaseImpl
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCaseImpl
import com.sayler666.gina.friends.usecase.GetFriendUseCase
import com.sayler666.gina.friends.usecase.GetFriendUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FriendsModule {

    @Binds
    abstract fun addFriendUseCase(useCase: AddFriendUseCaseImpl): AddFriendUseCase

    @Binds
    abstract fun editFriendUseCase(useCase: EditFriendUseCaseImpl): EditFriendUseCase

    @Binds
    abstract fun deleteFriendUseCase(useCase: DeleteFriendUseCaseImpl): DeleteFriendUseCase

    @Binds
    abstract fun getAllFriendsUseCase(useCase: GetAllFriendsUseCaseImpl): GetAllFriendsUseCase

    @Binds
    abstract fun getAllFriendsByRecentUseCase(useCase: GetAllFriendsByRecentUseCaseImpl): GetAllFriendsByRecentUseCase

    @Binds
    abstract fun getFriendUseCase(useCase: GetFriendUseCaseImpl): GetFriendUseCase
}
