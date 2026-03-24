package com.sayler666.gina.feature.setup.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewAction.NavigateToJournal
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewEvent.OnDatabaseSelected
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewEvent.OnNewDatabaseCreated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : ViewModel() {

    private val mutableViewState = MutableStateFlow<ViewState>(ViewState)
    val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnDatabaseSelected -> viewModelScope.launch {
                if (ginaDatabaseProvider.importFromUri(event.uri))
                    mutableViewActions.trySend(NavigateToJournal)
            }
            is OnNewDatabaseCreated -> viewModelScope.launch {
                if (ginaDatabaseProvider.createNewDb(event.uri))
                    mutableViewActions.trySend(NavigateToJournal)
            }
        }
    }

    data object ViewState

    sealed interface ViewEvent {
        data class OnDatabaseSelected(val uri: Uri) : ViewEvent
        data class OnNewDatabaseCreated(val uri: Uri) : ViewEvent
    }

    sealed interface ViewAction {
        data object NavigateToJournal : ViewAction
    }
}
