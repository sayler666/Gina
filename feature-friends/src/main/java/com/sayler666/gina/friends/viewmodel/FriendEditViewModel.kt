package com.sayler666.gina.friends.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.image.ImageOptimization
import com.sayler666.domain.model.journal.Friend
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.friends.usecase.DeleteFriendUseCase
import com.sayler666.gina.friends.usecase.EditFriendUseCase
import com.sayler666.gina.friends.usecase.GetFriendUseCase
import com.sayler666.gina.friends.viewmodel.FriendEditViewModel.ViewAction.Dismiss
import com.sayler666.gina.friends.viewmodel.FriendEditViewModel.ViewEvent.OnChangeAvatar
import com.sayler666.gina.friends.viewmodel.FriendEditViewModel.ViewEvent.OnChangeName
import com.sayler666.gina.friends.viewmodel.FriendEditViewModel.ViewEvent.OnClearAvatar
import com.sayler666.gina.friends.viewmodel.FriendEditViewModel.ViewEvent.OnDeleteFriend
import com.sayler666.gina.friends.viewmodel.FriendEditViewModel.ViewEvent.OnLoadFriend
import com.sayler666.gina.friends.viewmodel.FriendEditViewModel.ViewEvent.OnUpdateFriend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendEditViewModel @Inject constructor(
    private val friendsMapper: FriendsMapper,
    private val getFriendUseCase: GetFriendUseCase,
    private val editFriendUseCase: EditFriendUseCase,
    private val deleteFriendUseCase: DeleteFriendUseCase,
    private val imageOptimization: ImageOptimization
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    private val _friend = MutableStateFlow<Friend?>(null)
    private var loadFriendJob: Job? = null

    private val mutableViewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    init {
        observeFriend()
    }

    private fun observeFriend() {
        _friend
            .filterNotNull()
            .map(friendsMapper::mapToFriend)
            .onEach { friend ->
                mutableViewState.update { it.copy(friend = friend) }
            }.launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnLoadFriend -> observeFriend(event.id)
            is OnChangeName -> _friend.update { it?.copy(name = event.name) }
            is OnUpdateFriend -> updateFriend()
            is OnDeleteFriend -> deleteFriend()
            is OnChangeAvatar -> changeAvatar(event.avatar)
            is OnClearAvatar -> _friend.update { it?.copy(avatar = null) }
        }
    }

    private fun observeFriend(id: Int) {
        loadFriendJob?.cancel()
        loadFriendJob = getFriendUseCase.getFriendFlow(id)
            .onEach { _friend.value = it }
            .launchIn(viewModelScope)
    }

    private fun updateFriend() {
        viewModelScope.launch(SupervisorJob() + exceptionHandler) {
            _friend.value?.let {
                editFriendUseCase.editFriend(it)
            }
            mutableViewActions.trySend(Dismiss)
        }
    }

    private fun deleteFriend() {
        loadFriendJob?.cancel()
        viewModelScope.launch(SupervisorJob() + exceptionHandler) {
            _friend.value?.let {
                deleteFriendUseCase.deleteFriend(it)
            }
            mutableViewActions.trySend(Dismiss)
        }
    }

    private fun changeAvatar(avatar: ByteArray) {
        viewModelScope.launch {
            val compressedAvatar = imageOptimization.optimizeImage(avatar)
            _friend.update { it?.copy(avatar = compressedAvatar) }
        }
    }

    data class ViewState(val friend: FriendState? = null)

    sealed interface ViewEvent {
        data class OnLoadFriend(val id: Int) : ViewEvent
        data class OnChangeName(val name: String) : ViewEvent
        data object OnUpdateFriend : ViewEvent
        data object OnDeleteFriend : ViewEvent
        data class OnChangeAvatar(val avatar: ByteArray) : ViewEvent
        data object OnClearAvatar : ViewEvent
    }

    sealed interface ViewAction {
        data object Dismiss : ViewAction
    }
}
