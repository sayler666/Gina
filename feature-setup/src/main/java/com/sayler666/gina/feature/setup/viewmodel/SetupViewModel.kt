package com.sayler666.gina.feature.setup.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.data.database.db.journal.DatabaseFileManager
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewAction.RestartApp
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewEvent.OnDatabaseSelected
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewEvent.OnNewDatabaseCreated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val databaseFileManager: DatabaseFileManager
) : ViewModel() {

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnDatabaseSelected -> viewModelScope.launch {
                if (databaseFileManager.importFromUri(event.uri))
                    mutableViewActions.trySend(RestartApp)
            }
            is OnNewDatabaseCreated -> viewModelScope.launch {
                if (databaseFileManager.createNewDb(event.uri))
                    mutableViewActions.trySend(RestartApp)
            }
        }
    }

    sealed interface ViewEvent {
        data class OnDatabaseSelected(val uri: Uri) : ViewEvent
        data class OnNewDatabaseCreated(val uri: Uri) : ViewEvent
    }

    sealed interface ViewAction {
        data object RestartApp : ViewAction
    }
}
