package com.sayler666.gina.friends.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.friends.ui.ManageFriendsScreen
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.navigation.routes.ManageFriends
import com.sayler666.gina.navigation.routes.Route


fun EntryProviderScope<Route>.featureFriendsEntryBuilder() {
    entry<ManageFriends> {
        val activity = LocalActivity.current as ComponentActivity
        val viewModel: ManageFriendsViewModel = hiltViewModel(activity)
        ManageFriendsScreen(viewModel = viewModel)
    }
}