package com.sayler666.gina.di

import com.sayler666.gina.addDay.usecase.AddDayUseCase
import com.sayler666.gina.addDay.usecase.AddDayUseCaseImpl
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCaseImpl
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCaseImpl
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCaseImpl
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
    abstract fun provideEditDayUseCase(useCase: EditDayUseCaseImpl): EditDayUseCase

    @Binds
    abstract fun provideDeleteDayUseCase(useCase: DeleteDayUseCaseImpl): DeleteDayUseCase

    @Binds
    abstract fun provideAddDayUseCase(useCase: AddDayUseCaseImpl): AddDayUseCase
}
