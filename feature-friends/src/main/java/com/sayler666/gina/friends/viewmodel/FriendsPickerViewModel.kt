package com.sayler666.gina.friends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.FriendWithCount
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsByRecentUseCase
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewAction.FriendsPicked
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnDismissed
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnFriendToggled
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnInitialized
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnNewFriendAdded
import com.sayler666.gina.friends.viewmodel.FriendsPickerViewModel.ViewEvent.OnQueryChanged
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class FriendsPickerState(
    val friends: List<FriendState> = emptyList(),
    val selectedFriends: List<FriendState> = emptyList(),
    val searchQuery: String = "",
)

@HiltViewModel
class FriendsPickerViewModel @Inject constructor(
    private val getAllFriendsByRecentUseCase: GetAllFriendsByRecentUseCase,
    private val friendsMapper: FriendsMapper,
    private val addFriendUseCase: AddFriendUseCase,
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    private val _selectedFriendIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _searchQuery = MutableStateFlow<String?>(null)
    private val _allFriends = MutableStateFlow<List<FriendWithCount>>(emptyList())

    private val mutableViewState = MutableStateFlow(FriendsPickerState())
    val viewState: StateFlow<FriendsPickerState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private var initialized = false

    init {
        observeAllFriends()
        observeViewState()
    }

    private fun observeAllFriends() {
        getAllFriendsByRecentUseCase().onEach { _allFriends.value = it }.launchIn(viewModelScope)
    }

    private fun observeViewState() {
        combine(_allFriends, _selectedFriendIds, _searchQuery) { all, selectedIds, query ->
            FriendsPickerState(
                friends = friendsMapper.mapToDayFriends(selectedIds, all, query),
                selectedFriends = friendsMapper.mapToDayFriends(selectedIds, all, null)
                    .filter { it.selected },
                searchQuery = query ?: "",
            )
        }.onEach { mutableViewState.value = it }.launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnInitialized -> {
                if (initialized) return
                initialized = true
                _selectedFriendIds.value = event.friends.map { it.id }.toSet()
            }
            is OnQueryChanged -> _searchQuery.value = event.query.takeIf { it.isNotEmpty() }
            is OnFriendToggled -> _selectedFriendIds.value = when (event.selected) {
                true -> _selectedFriendIds.value + event.friendId
                false -> _selectedFriendIds.value - event.friendId
            }
            is OnNewFriendAdded -> viewModelScope.launch(exceptionHandler) {
                addFriendUseCase.addFriend(event.name)
                _searchQuery.value = null
            }
            OnDismissed -> {
                _searchQuery.value = null
                mutableViewActions.trySend(FriendsPicked(buildSelectedFriends()))
            }
        }
    }

    private fun buildSelectedFriends(): List<Friend> {
        val selectedIds = _selectedFriendIds.value
        return _allFriends.value
            .filter { it.friendId in selectedIds }
            .map { Friend(id = it.friendId, name = it.friendName, avatar = it.friendAvatar) }
    }

    sealed interface ViewEvent {
        data class OnInitialized(val friends: List<Friend>) : ViewEvent
        data class OnQueryChanged(val query: String) : ViewEvent
        data class OnFriendToggled(val friendId: Int, val selected: Boolean) : ViewEvent
        data class OnNewFriendAdded(val name: String) : ViewEvent
        data object OnDismissed : ViewEvent
    }

    sealed interface ViewAction {
        data class FriendsPicked(val friends: List<Friend>) : ViewAction
    }
}
