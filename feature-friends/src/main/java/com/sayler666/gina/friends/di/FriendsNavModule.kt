package com.sayler666.gina.friends.di

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sayler666.gina.friends.ui.ManageFriendsScreen
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.ManageFriends
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object FriendsNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        entry<ManageFriends> {
            val activity = LocalActivity.current as ComponentActivity
            val viewModel: ManageFriendsViewModel = hiltViewModel(activity)
            ManageFriendsScreen(viewModel = viewModel)
        }
    }
}
