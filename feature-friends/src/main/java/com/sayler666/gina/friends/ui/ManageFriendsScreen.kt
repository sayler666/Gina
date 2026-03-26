package com.sayler666.gina.friends.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewAction.Back
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent.OnAddNewFriend
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent.OnSearchChanged
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewState
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.LocalNavigator
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFriendsScreen(
    viewModel: ManageFriendsViewModel = hiltViewModel(),
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    BackHandler { viewModel.onViewEvent(OnBackPressed) }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            Back -> navigator.back()
        }
    }

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
    val hazeState = rememberHazeState()
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FriendsList(
            padding = PaddingValues(top = topPadding, start = 12.dp, end = 12.dp),
            hazeState = hazeState,
            friends = viewState.friends,
            selectable = false,
            onFriendClicked = { id, _ ->
                friendIdToEdit.value = id
                showFriendEditPopup.value = true
            },
            onAddNewFriend = { viewEvent(OnAddNewFriend(it)) },
            onSearchChanged = { viewEvent(OnSearchChanged(it)) },
            searchValue = viewState.searchQuery
        )

        Column(
            modifier = Modifier.hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 24.dp,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    tint = HazeTint(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                )
            ) {
                progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
            }
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.friends_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = { viewEvent(OnBackPressed) }) {
                        Icon(Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }

        if (friendIdToEdit.value != null && showFriendEditPopup.value) {
            FriendEdit(
                showFriendEditPopup.value,
                friendIdToEdit.value!!,
                onDismiss = { showFriendEditPopup.value = false }
            )
        }
    }
}
