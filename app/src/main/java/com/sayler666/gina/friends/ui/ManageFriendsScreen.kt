package com.sayler666.gina.friends.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.ui.NavigationBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun ManageFriendsScreen(
    navController: NavController,
    vm: GinaMainViewModel = hiltViewModel(),
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme, color = MaterialTheme.colorScheme.surface)

    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavGraphs.root.route)
    }
    val viewModel: ManageFriendsViewModel = hiltViewModel(backStackEntry)
    val friends: List<FriendEntity> by viewModel.friends.collectAsStateWithLifecycle()

    val friendIdToEdit = remember { mutableStateOf<Int?>(null) }
    val showFriendEditPopup = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Friends") })
        },
        content = { padding ->
            FriendsList(
                padding = padding,
                friends = friends,
                selectable = false,
                onFriendClicked = { id, _ ->
                    friendIdToEdit.value = id
                    showFriendEditPopup.value = true
                },
                onAddNewFriend = {
                    searchQuery.value = ""
                    viewModel.searchFriend("")
                    viewModel.addNewFriend(it)
                },
                onSearchChanged = {
                    searchQuery.value = it
                    viewModel.searchFriend(it)
                },
                searchValue = searchQuery.value
            )

            if (friendIdToEdit.value != null && showFriendEditPopup.value) FriendEdit(
                showFriendEditPopup.value,
                friendIdToEdit.value!!,
                onDismiss = { showFriendEditPopup.value = false }
            )
        })
}
