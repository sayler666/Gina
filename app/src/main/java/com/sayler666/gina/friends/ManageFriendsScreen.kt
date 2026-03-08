package com.sayler666.gina.friends

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.ui.LocalTheme
import com.sayler666.gina.friends.ui.ManageFriendsScreen as FeatureManageFriendsScreen

@Composable
fun ManageFriendsScreen() {
    val theme = LocalTheme.current
    val activity = LocalActivity.current as ComponentActivity
    val viewModel: ManageFriendsViewModel = hiltViewModel(activity)
    FeatureManageFriendsScreen(viewModel = viewModel, theme = theme)
}
