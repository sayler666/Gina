package com.sayler666.gina.day.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.domain.model.journal.Friend
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.friends.ui.FriendsPicker
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewAction.FriendsPicked
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnDismissed
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnFriendToggled
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnInitialized
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnNewFriendAdded
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnQueryChanged
import java.util.UUID

@Composable
fun FriendsPickerButton(
    initialFriends: List<Friend>,
    onFriendsPicked: (List<Friend>) -> Unit,
) {
    val vmKey = remember { UUID.randomUUID().toString() }
    val viewModel: FriendsPickerViewModel = hiltViewModel(key = vmKey)
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(initialFriends) {
        if (initialFriends.isNotEmpty()) viewModel.onViewEvent(OnInitialized(initialFriends))
    }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            is FriendsPicked -> onFriendsPicked(action.friends)
        }
    }

    val showPicker = remember { mutableStateOf(false) }

    when (state.selectedFriends.isNotEmpty()) {
        true -> Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .clickable(
                    indication = ripple(bounded = false),
                    interactionSource = remember { MutableInteractionSource() }
                ) { showPicker.value = true }
        ) {
            state.selectedFriends.take(2).forEachIndexed { i, friend ->
                FriendIcon(
                    friend = friend,
                    size = 32.dp,
                    modifier = Modifier
                        .offset(i * 8.dp)
                        .zIndex(-i.toFloat())
                )
            }
        }

        false -> IconButton(onClick = { showPicker.value = true }) {
            Icon(
                painter = rememberVectorPainter(image = Filled.People),
                contentDescription = null,
            )
        }
    }

    if (showPicker.value) {
        FriendsPicker(
            searchValue = state.searchQuery,
            onDismiss = {
                showPicker.value = false
                viewModel.onViewEvent(OnDismissed)
            },
            onSearchChanged = { viewModel.onViewEvent(OnQueryChanged(it)) },
            onAddNewFriend = { viewModel.onViewEvent(OnNewFriendAdded(it)) },
            onFriendClicked = { id, selected ->
                viewModel.onViewEvent(OnFriendToggled(id, selected))
            },
            friends = state.friends,
        )
    }
}
