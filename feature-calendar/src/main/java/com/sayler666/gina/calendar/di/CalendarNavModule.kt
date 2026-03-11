package com.sayler666.gina.calendar.di

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sayler666.gina.calendar.ui.CalendarScreen
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.routes.Calendar
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object CalendarNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        entry<Calendar> {
            val activity = LocalActivity.current as ComponentActivity
            val viewModel: CalendarViewModel = hiltViewModel(activity)
            CalendarScreen(viewModel = viewModel)
        }
    }
}
