package com.sayler666.gina.friends.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
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
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .padding(top = 46.dp)
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
            }
        })
}
