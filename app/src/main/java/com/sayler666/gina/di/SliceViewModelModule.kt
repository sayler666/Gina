package com.sayler666.gina.di

import com.sayler666.gina.reminder.viewmodel.RemindersViewModel
import com.sayler666.gina.reminder.viewmodel.RemindersViewModelImpl
import com.sayler666.gina.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.settings.viewmodel.ImageOptimizationViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SliceViewModelModule {

    @Binds
    abstract fun provideImageOptimizationViewModel(viewModel: ImageOptimizationViewModelImpl): ImageOptimizationViewModel

    @Binds
    abstract fun provideRemindersViewModel(viewModel: RemindersViewModelImpl): RemindersViewModel
}
