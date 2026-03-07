package com.sayler666.gina.di

import com.sayler666.gina.feature.settings.reminder.RemindersViewModel
import com.sayler666.gina.reminder.viewmodel.RemindersViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SliceViewModelModule {

    @Binds
    abstract fun provideRemindersViewModel(viewModel: RemindersViewModelImpl): RemindersViewModel
}
