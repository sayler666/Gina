package com.sayler666.gina.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.dayDetails.viewmodel.FriendsMapper
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteFriendUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditFriendUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.GetFriendUseCase
import com.sayler666.gina.db.Friend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendEditViewModel @Inject constructor(
    private val friendsMapper: FriendsMapper,
    private val getFriendUseCase: GetFriendUseCase,
    private val editFriendUseCase: EditFriendUseCase,
    private val deleteFriendUseCase: DeleteFriendUseCase,
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    private val _friend = MutableStateFlow<Friend?>(null)
    val friend: StateFlow<FriendEntity?>
        get() = _friend
            .filterNotNull()
            .map(friendsMapper::mapToFriend)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(500),
                null
            )

    fun loadFriend(id: Int) {
        viewModelScope.launch {
            getFriendUseCase.getFriend(id).collect {
                _friend.value = it
            }
        }
    }

    fun deleteFriend() {
        viewModelScope.launch(SupervisorJob() + exceptionHandler) {
            _friend.value?.let {
                //deleteFriendUseCase.deleteFriend(it)
            }
        }
    }

    fun updateFriend() {
        viewModelScope.launch(SupervisorJob() + exceptionHandler) {
            _friend.value?.let {
                //editFriendUseCase.editFriend(it)
            }
        }
    }

}
