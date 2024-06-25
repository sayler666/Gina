package com.sayler666.gina.insights.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.ginaApp.navigation.BottomNavigationVisibilityManager
import com.sayler666.gina.insights.viewmodel.InsightState.LoadingState
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnLockBottomBar
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnUnlockBottomBar
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import com.sayler666.gina.mood.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val getDaysUseCase: GetDaysUseCase,
    private val insightsMapper: InsightsMapper,
    private val getAllFriendsUseCase: GetAllFriendsUseCase,
    private val friendsMapper: FriendsMapper,
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _moodFilters = MutableStateFlow<List<Mood>>(Mood.entries)
    val moodFilters: StateFlow<List<Mood>> = _moodFilters.asStateFlow()

    val filtersActive: StateFlow<Boolean> = _moodFilters.map { moods ->
        moods.size != Mood.entries.size
    }.stateIn(
        viewModelScope,
        WhileSubscribed(500),
        false
    )

    private val _state = MutableStateFlow<InsightState>(LoadingState)
    val state: StateFlow<InsightState> = _state

    init {
        initDb()
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnLockBottomBar -> bottomNavigationVisibilityManager.lockHide()
            OnUnlockBottomBar -> bottomNavigationVisibilityManager.unlockAndShow()
        }
    }

    private fun initDb() {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        viewModelScope.launch {
            combine(
                _moodFilters,
                _searchQuery
            ) { moods, search ->
                moods to search
            }.flatMapLatest { (moods, search) ->
                val friendsLastMonthDeferred = async {
                    getAllFriendsUseCase
                        .getAllFriendsWithCount(
                            searchQuery = search,
                            moods = moods,
                            dateFrom = LocalDate.now().minusMonths(1),
                            dateTo = LocalDate.now()
                        )
                        .let { friendsMapper.mapToFriends(it) }
                }
                val friendsAllTimeDeferred = async {
                    getAllFriendsUseCase
                        .getAllFriendsWithCount(
                            searchQuery = search,
                            moods = moods,
                            dateFrom = LocalDate.now().minusYears(100),
                            dateTo = LocalDate.now()
                        )
                        .let { friendsMapper.mapToFriends(it) }
                }

                getDaysUseCase
                    .getFilteredDaysFlow(search, moods)
                    .map {
                        insightsMapper.toInsightsState(
                            days = it,
                            searchQuery = search,
                            moods = moods,
                            friendsLastMonth = friendsLastMonthDeferred.await(),
                            friendsAllTime = friendsAllTimeDeferred.await()
                        )
                    }
            }.collect(_state::tryEmit)
        }
    }

    fun searchQuery(searchQuery: String) {
        _searchQuery.update { searchQuery }
    }

    fun updateMoodFilters(moods: List<Mood>) {
        _moodFilters.update { moods }
    }

    fun resetFilters() {
        _moodFilters.update { Mood.entries }
    }

    sealed interface ViewEvent {
        // TODO add rest of the events
        data object OnLockBottomBar : ViewEvent
        data object OnUnlockBottomBar : ViewEvent
    }
}
