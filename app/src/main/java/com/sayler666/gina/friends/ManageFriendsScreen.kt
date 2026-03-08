package com.sayler666.gina.friends

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.ui.LocalTheme
import com.sayler666.gina.friends.ui.ManageFriendsScreen as FeatureManageFriendsScreen

@Composable
fun ManageFriendsScreen() {
    val theme = LocalTheme.current
    val activity = LocalContext.current as ComponentActivity
    val viewModel: ManageFriendsViewModel = hiltViewModel(activity)
    FeatureManageFriendsScreen(viewModel = viewModel, theme = theme)
}
