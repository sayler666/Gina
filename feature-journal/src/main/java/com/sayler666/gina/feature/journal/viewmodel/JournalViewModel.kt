package com.sayler666.gina.feature.journal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.navigation.BottomNavigationVisibilityManager
import com.sayler666.data.database.db.journal.usecase.GetDaysUseCase
import com.sayler666.domain.model.journal.AttachmentWithDay
import com.sayler666.gina.feature.journal.usecase.PreviousYearsAttachmentsUseCase
import com.sayler666.gina.feature.journal.viewmodel.JournalState.LoadingState
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewAction.NavToAttachmentPreview
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewAction.NavToDay
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnAttachmentClick
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnDayClick
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnFiltersChanged
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnResetFilters
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.feature.settings.SettingsStorage
import com.sayler666.gina.ui.filters.FiltersState
import com.sayler666.gina.ui.filters.toDateBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val getDaysUseCase: GetDaysUseCase,
    private val daysMapper: DaysMapper,
    private val previousYearsAttachmentsUseCase: PreviousYearsAttachmentsUseCase,
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val mutableViewState = MutableStateFlow<JournalState>(createInitialState())
    val viewState: StateFlow<JournalState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private val mutableFiltersState = MutableStateFlow(FiltersState())
    val filtersState: StateFlow<FiltersState> = mutableFiltersState.asStateFlow()

    private var journalStateJob: Job? = null

    init {
        observeJournalState()
    }

    private data class JournalParams(
        val filters: FiltersState,
        val attachments: List<AttachmentWithDay>,
        val incognito: Boolean
    )

    private fun observeJournalState() {
        journalStateJob?.cancel()
        journalStateJob = combine(
            mutableFiltersState,
            previousYearsAttachmentsUseCase(),
            settingsStorage.getIncognitoModeFlow(),
            ::JournalParams
        ).flatMapLatest { (filters, attachments, incognito) ->
            val (dateFrom, dateTo) = filters.dateRange?.toDateBounds() ?: (null to null)
            getDaysUseCase.getFilteredDaysFlow(
                searchQuery = filters.searchQuery,
                moods = filters.moods,
                dateFrom = dateFrom,
                dateTo = dateTo,
            ).map { days ->
                daysMapper.toJournalState(
                    days = days,
                    searchQuery = filters.searchQuery,
                    filtersActive = filters.filtersActive,
                    previousYearsAttachments = attachments,
                    incognitoMode = incognito
                )
            }
        }
            .onEach(mutableViewState::tryEmit)
            .launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnFiltersChanged -> updateFilters(event.filters)
            OnResetFilters -> updateFilters(FiltersState())
            is OnAttachmentClick -> navToAttachment(event)
            is OnDayClick -> navToDay(event)
            OnHideBottomBar -> bottomNavigationVisibilityManager.hide()
            OnShowBottomBar -> bottomNavigationVisibilityManager.show()
        }
    }

    private fun updateFilters(new: FiltersState) {
        val old = mutableFiltersState.value
        if (old.searchVisible != new.searchVisible) {
            if (new.searchVisible) bottomNavigationVisibilityManager.lockHide()
            else bottomNavigationVisibilityManager.unlockAndShow()
        }
        mutableFiltersState.update { new }
    }

    private fun navToDay(event: OnDayClick) {
        mutableViewActions.trySend(NavToDay(event.dayId))
    }

    private fun navToAttachment(event: OnAttachmentClick) {
        val attachmentIds = (mutableViewState.value as? JournalState.DaysState)
            ?.previousYearsAttachments
            ?.mapNotNull { it.state.id }
            ?: emptyList()
        mutableViewActions.trySend(NavToAttachmentPreview(event.imageId, attachmentIds))
    }

    private fun createInitialState() = LoadingState

    sealed interface ViewEvent {
        data class OnDayClick(val dayId: Int) : ViewEvent
        data class OnAttachmentClick(val imageId: Int) : ViewEvent
        data class OnFiltersChanged(val filters: FiltersState) : ViewEvent
        data object OnResetFilters : ViewEvent
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
    }

    sealed interface ViewAction {
        data class NavToDay(val dayId: Int) : ViewAction
        data class NavToAttachmentPreview(val imageId: Int, val attachmentIds: List<Int>) :
            ViewAction
    }
}
