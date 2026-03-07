package com.sayler666.gina.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.destinations.ManageFriendsScreenDestination
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.feature.settings.ui.SettingsScreen as FeatureSettingsScreen

@RootNavGraph
@Destination
@Composable
fun SettingsScreen(
    destinationsNavigator: DestinationsNavigator,
    vm: GinaMainViewModel = hiltViewModel(),
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    FeatureSettingsScreen(
        theme = theme,
        onNavigateToFriends = { destinationsNavigator.navigate(ManageFriendsScreenDestination) },
        onNavigateBack = { destinationsNavigator.popBackStack() }
    )
}
