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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFriendsScreen(
    viewModel: ManageFriendsViewModel,
    theme: Theme?,
) {
    NavigationBarColor(theme = theme, color = MaterialTheme.colorScheme.surface)

    val friends: List<FriendState> by viewModel.friends.collectAsStateWithLifecycle()

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
