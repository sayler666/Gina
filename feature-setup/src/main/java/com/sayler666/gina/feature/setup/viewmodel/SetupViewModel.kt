package com.sayler666.gina.feature.setup.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewAction.NavigateToJournal
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewEvent.OnDatabaseSelected
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewEvent.OnPermissionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(createInitialState())
    val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private fun createInitialState() = ViewState(
        permissionGranted = Environment.isExternalStorageManager()
    )

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnPermissionResult -> mutableViewState.update {
                it.copy(permissionGranted = Environment.isExternalStorageManager())
            }
            is OnDatabaseSelected -> viewModelScope.launch {
                if (ginaDatabaseProvider.openAndRememberDB(event.path))
                    mutableViewActions.trySend(NavigateToJournal)
            }
        }
    }

    data class ViewState(
        val permissionGranted: Boolean
    )

    sealed interface ViewEvent {
        data object OnPermissionResult : ViewEvent
        data class OnDatabaseSelected(val path: String) : ViewEvent
    }

    sealed interface ViewAction {
        data object NavigateToJournal : ViewAction
    }
}
