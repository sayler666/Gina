package com.sayler666.gina.friends

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.friends.ui.ManageFriendsScreen as FeatureManageFriendsScreen

@Destination
@Composable
fun ManageFriendsScreen(
    navController: NavController,
    vm: GinaMainViewModel = hiltViewModel(),
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavGraphs.root.route)
    }
    val viewModel: ManageFriendsViewModel = hiltViewModel(backStackEntry)
    FeatureManageFriendsScreen(viewModel = viewModel, theme = theme)
}
