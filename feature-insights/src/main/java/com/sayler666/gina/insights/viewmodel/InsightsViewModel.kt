package com.sayler666.gina.insights.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.navigation.BottomNavigationVisibilityManager
import com.sayler666.data.database.db.journal.usecase.GetDaysUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.insights.usecase.GetAvgMoodByMonthsUseCase
import com.sayler666.gina.insights.usecase.GetAvgMoodByWeeksUseCase
import com.sayler666.gina.insights.viewmodel.InsightState.LoadingState
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnFiltersChanged
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnResetFilters
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.ui.filters.FiltersState
import com.sayler666.gina.ui.filters.toDateBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getDaysUseCase: GetDaysUseCase,
    private val insightsMapper: InsightsMapper,
    private val getAllFriendsUseCase: GetAllFriendsUseCase,
    private val getAvgMoodByMonthsUseCase: GetAvgMoodByMonthsUseCase,
    private val getAvgMoodByWeeksUseCase: GetAvgMoodByWeeksUseCase,
    private val friendsMapper: FriendsMapper,
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
) : ViewModel() {

    private val mutableFiltersState = MutableStateFlow(FiltersState())
    val filtersState: StateFlow<FiltersState> = mutableFiltersState.asStateFlow()

    private val _state = MutableStateFlow<InsightState>(LoadingState)
    val state: StateFlow<InsightState> = _state

    init {
        observeInsights()
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnFiltersChanged -> updateFilters(event.filters)
            OnResetFilters -> updateFilters(FiltersState())
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

    private fun observeInsights() {
        viewModelScope.launch {
            mutableFiltersState
                .flatMapLatest { filters ->
                    val (dateFrom, dateTo) = filters.dateRange?.toDateBounds() ?: (null to null)
                    val friendsLastMonthDeferred = async {
                        getAllFriendsUseCase
                            .getAllFriendsWithCount(
                                searchQuery = filters.searchQuery,
                                moods = filters.moods,
                                dateFrom = LocalDate.now().minusMonths(1),
                                dateTo = LocalDate.now()
                            )
                            .let { friendsMapper.mapToFriends(it) }
                    }
                    val friendsAllTimeDeferred = async {
                        getAllFriendsUseCase
                            .getAllFriendsWithCount(
                                searchQuery = filters.searchQuery,
                                moods = filters.moods,
                                dateFrom = dateFrom ?: LocalDate.now().minusYears(100),
                                dateTo = dateTo ?: LocalDate.now()
                            )
                            .let { friendsMapper.mapToFriends(it) }
                    }

                    val avgMoodByMonth = async { getAvgMoodByMonthsUseCase() }
                    val avgMoodByWeek = async { getAvgMoodByWeeksUseCase() }

                    getDaysUseCase
                        .getFilteredDaysFlow(
                            searchQuery = filters.searchQuery,
                            moods = filters.moods,
                            dateFrom = dateFrom,
                            dateTo = dateTo,
                        )
                        .map {
                            insightsMapper.toInsightsState(
                                days = it,
                                searchQuery = filters.searchQuery,
                                moods = filters.moods,
                                moodsByMonth = avgMoodByMonth.await(),
                                moodsByWeek = avgMoodByWeek.await(),
                                friendsLastMonth = friendsLastMonthDeferred.await(),
                                friendsAllTime = friendsAllTimeDeferred.await()
                            )
                        }
                }.collect(_state::tryEmit)
        }
    }

    sealed interface ViewEvent {
        data class OnFiltersChanged(val filters: FiltersState) : ViewEvent
        data object OnResetFilters : ViewEvent
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
    }
}
