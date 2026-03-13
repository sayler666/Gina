package com.sayler666.gina.friends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent.OnAddNewFriend
import com.sayler666.gina.friends.viewmodel.ManageFriendsViewModel.ViewEvent.OnSearchChanged
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageFriendsViewModel @Inject constructor(
    private val getAllFriendsUseCase: GetAllFriendsUseCase,
    private val friendsMapper: FriendsMapper,
    private val addFriendUseCase: AddFriendUseCase
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    private val searchQuery = MutableStateFlow("")

    private val mutableViewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

    init {
        observeFriends()
    }

    private fun observeFriends() {
        combine(getAllFriendsUseCase.getAllFriendsWithCount(), searchQuery) { friends, query ->
            friendsMapper.mapToFriends(friends, query)
        }.onEach { friends ->
            mutableViewState.update { it.copy(friends = friends) }
        }.launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnSearchChanged -> {
                searchQuery.value = event.query
                mutableViewState.update { it.copy(searchQuery = event.query) }
            }

            is OnAddNewFriend -> {
                searchQuery.value = ""
                mutableViewState.update { it.copy(searchQuery = "") }
                viewModelScope.launch(SupervisorJob() + exceptionHandler) {
                    addFriendUseCase.addFriend(event.name)
                }
            }
        }
    }

    data class ViewState(
        val friends: List<FriendState> = emptyList(),
        val searchQuery: String = ""
    )

    sealed interface ViewEvent {
        data class OnSearchChanged(val query: String) : ViewEvent
        data class OnAddNewFriend(val name: String) : ViewEvent
    }
}
