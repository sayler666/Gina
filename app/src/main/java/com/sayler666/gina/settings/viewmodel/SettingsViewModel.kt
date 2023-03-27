package com.sayler666.gina.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.imageCompressor.ImageCompressor.CompressorSettings
import com.sayler666.gina.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val setting: Settings,
    getAllFriendsUseCase: GetAllFriendsUseCase,
    friendsMapper: FriendsMapper,
    private val addFriendUseCase: AddFriendUseCase,
    private val databaseProvider: DatabaseProvider
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    private val _friends = getAllFriendsUseCase.getAllFriends().stateIn(
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

    fun removeFriend(friendId: Int) {
        viewModelScope.launch(SupervisorJob() + exceptionHandler) {
            //addFriendUseCase.remo(friendName)
        }
    }

    private val _tempImageCompressorSettings: MutableStateFlow<CompressorSettings> =
        MutableStateFlow(CompressorSettings())
    val imageCompressorSettings: StateFlow<CompressorSettings?>
        get() = setting.getImageCompressorSettingsFlow()
            .map {
                _tempImageCompressorSettings.value = it
                it
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(500),
                null
            )

    private val _databasePath: MutableStateFlow<String?> = MutableStateFlow(null)
    val databasePath: StateFlow<String?>
        get() = setting.getDatabasePathFlow()
            .map {
                _databasePath.value = it
                it
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(500),
                null
            )

    fun setNewImageQuality(quality: Int) {
        val settings = _tempImageCompressorSettings.value

        viewModelScope.launch {
            setting.saveImageCompressorSettings(settings.copy(quality = quality))
        }
    }

    fun setNewImageSize(size: Long) {
        val settings = _tempImageCompressorSettings.value

        viewModelScope.launch {
            setting.saveImageCompressorSettings(settings.copy(size = size))
        }
    }

    fun openDatabase(path: String) {
        viewModelScope.launch {
            databaseProvider.openDB(path)
        }
    }
}
