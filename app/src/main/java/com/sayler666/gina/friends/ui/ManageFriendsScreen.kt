package com.sayler666.gina.friends.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Destination
@Composable
fun ManageFriendsScreen(
    navController: NavController
) {
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
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(8.dp)
            ) {
                FriendsList(
                    friends = friends,
                    selectable = false,
                    onFriendClicked = { id, _ ->
                        friendIdToEdit.value = id
                        showFriendEditPopup.value = true
                    },
                    onAddNewFriend = {
                        searchQuery.value = ""
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
            }
        })
}
