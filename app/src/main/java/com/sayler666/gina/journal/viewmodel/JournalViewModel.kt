package com.sayler666.gina.journal.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.ginaApp.navigation.BottomNavigationVisibilityManager
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import com.sayler666.gina.journal.usecase.PreviousYearsAttachmentsUseCase
import com.sayler666.gina.journal.viewmodel.JournalState.LoadingState
import com.sayler666.gina.journal.viewmodel.JournalState.PermissionNeededState
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewAction.NavToAttachmentPreview
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewAction.NavToDay
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewAction.NavToManageAllFilesSettings
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnAttachmentClick
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnDayClick
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnLockBottomBar
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnManageAllFilesSettingsClick
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnMoodFiltersChanged
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnRefreshPermissionStatus
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnResetFilters
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnSearchQueryChanged
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnUnlockBottomBar
import com.sayler666.gina.mood.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val getDaysUseCase: GetDaysUseCase,
    private val daysMapper: DaysMapper,
    private val previousYearsAttachmentsUseCase: PreviousYearsAttachmentsUseCase,
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(createInitialState())
    val viewState: StateFlow<JournalState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private val mutableSearchQuery = MutableStateFlow("")
    private val mutableMoodFilters = MutableStateFlow<List<Mood>>(Mood.entries)

    init {
        initDb()
    }

    private fun initDb() {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }

        combine(
            mutableMoodFilters,
            mutableSearchQuery,
            previousYearsAttachmentsUseCase()
        ) { moods, search, attachments ->
            getDaysUseCase
                .getFilteredDaysFlow(search, moods)
                .map { days ->
                    daysMapper.toJournalState(
                        days = days,
                        searchQuery = search,
                        moods = moods,
                        previousYearsAttachments = attachments
                    )
                }.onEach(mutableViewState::tryEmit)
                .launchIn(viewModelScope)
        }.launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnMoodFiltersChanged -> updateMoodFilters(event.moods)
            is OnSearchQueryChanged -> searchQuery(event.searchQuery)
            OnResetFilters -> resetFilters()
            OnRefreshPermissionStatus -> refreshPermissionStatus()
            is OnAttachmentClick -> navToAttachment(event)
            is OnDayClick -> navToDay(event)
            OnManageAllFilesSettingsClick -> navToManageAllFilesSettings()
            OnHideBottomBar -> bottomNavigationVisibilityManager.hide()
            OnShowBottomBar -> bottomNavigationVisibilityManager.show()
            OnLockBottomBar -> bottomNavigationVisibilityManager.lockHide()
            OnUnlockBottomBar -> bottomNavigationVisibilityManager.unlockAndShow()
        }
    }

    private fun navToManageAllFilesSettings() {
        mutableViewActions.trySend(NavToManageAllFilesSettings)
    }

    private fun navToDay(event: OnDayClick) {
        mutableViewActions.trySend(NavToDay(event.dayId))
    }

    private fun navToAttachment(event: OnAttachmentClick) {
        mutableViewActions.trySend(NavToAttachmentPreview(event.imageId))
    }

    private fun refreshPermissionStatus() {
        if (Environment.isExternalStorageManager()) initDb()
    }

    private fun searchQuery(searchQuery: String) {
        mutableSearchQuery.update { searchQuery }
    }

    private fun updateMoodFilters(moods: List<Mood>) {
        mutableMoodFilters.update { moods }
    }

    private fun resetFilters() {
        mutableMoodFilters.update { Mood.entries }
    }

    private fun createInitialState() =
        if (Environment.isExternalStorageManager()) LoadingState else PermissionNeededState

    sealed interface ViewEvent {
        data class OnDayClick(val dayId: Int) : ViewEvent
        data class OnAttachmentClick(val imageId: Int) : ViewEvent
        data object OnRefreshPermissionStatus : ViewEvent
        data object OnManageAllFilesSettingsClick : ViewEvent
        data class OnSearchQueryChanged(val searchQuery: String) : ViewEvent
        data class OnMoodFiltersChanged(val moods: List<Mood>) : ViewEvent
        data object OnResetFilters : ViewEvent
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
        data object OnLockBottomBar : ViewEvent
        data object OnUnlockBottomBar : ViewEvent
    }

    sealed interface ViewAction {
        data class NavToDay(val dayId: Int) : ViewAction
        data class NavToAttachmentPreview(val imageId: Int) : ViewAction
        data object NavToManageAllFilesSettings : ViewAction
    }
}
