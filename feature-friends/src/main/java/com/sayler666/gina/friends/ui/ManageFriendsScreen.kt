package com.sayler666.gina.friends.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent.OnAddNewFriend
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent.OnSearchChanged
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewState
import com.sayler666.gina.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFriendsScreen(
    viewModel: ManageFriendsViewModel = hiltViewModel(),
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    Content(
        viewState = viewState,
        viewEvent = viewModel::onViewEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    viewState: ViewState,
    viewEvent: (ViewEvent) -> Unit
) {
    val friendIdToEdit = remember { mutableStateOf<Int?>(null) }
    val showFriendEditPopup = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.friends_screen_title)) })
        },
        content = { padding: PaddingValues ->
            FriendsList(
                padding = padding,
                friends = viewState.friends,
                selectable = false,
                onFriendClicked = { id, _ ->
                    friendIdToEdit.value = id
                    showFriendEditPopup.value = true
                },
                onAddNewFriend = {
                    viewEvent(OnAddNewFriend(it))
                },
                onSearchChanged = {
                    viewEvent(OnSearchChanged(it))
                },
                searchValue = viewState.searchQuery
            )

            if (friendIdToEdit.value != null && showFriendEditPopup.value) FriendEdit(
                showFriendEditPopup.value,
                friendIdToEdit.value!!,
                onDismiss = { showFriendEditPopup.value = false }
            )
        }
    )
}
