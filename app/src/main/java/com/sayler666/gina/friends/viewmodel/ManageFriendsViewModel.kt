package com.sayler666.gina.friends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageFriendsViewModel @Inject constructor(
    getAllFriendsUseCase: GetAllFriendsUseCase,
    friendsMapper: FriendsMapper,
    private val addFriendUseCase: AddFriendUseCase
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    private val _friends = getAllFriendsUseCase.getAllFriendsWithCount().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        emptyList()
    )

    private val friendsSearchQuery: MutableStateFlow<String?> = MutableStateFlow(null)
    val friends: StateFlow<List<FriendEntity>> = combine(
        _friends,
        friendsSearchQuery
    ) { friends, friendsSearchQuery ->
        friendsMapper.mapToFriends(friends, friendsSearchQuery)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        emptyList()
    )

    fun searchFriend(searchQuery: String) {
        friendsSearchQuery.update { searchQuery }
    }

    fun addNewFriend(friendName: String) {
        viewModelScope.launch(SupervisorJob() + exceptionHandler) {
            addFriendUseCase.addFriend(friendName)
        }
    }

}
