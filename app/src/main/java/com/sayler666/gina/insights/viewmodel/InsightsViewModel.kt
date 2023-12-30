package com.sayler666.gina.insights.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.insights.viewmodel.InsightState.LoadingState
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val getDaysUseCase: GetDaysUseCase,
    private val insightsMapper: InsightsMapper,
    private val getAllFriendsUseCase: GetAllFriendsUseCase,
    private val friendsMapper: FriendsMapper,
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

    private fun initDb() {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        viewModelScope.launch {
            combine(
                _moodFilters,
                _searchQuery
            ) { moods, search ->
                moods to search
            }.flatMapLatest { (moods, search) ->
                val friendsDeferred = async {
                    getAllFriendsUseCase
                        .getAllFriendsWithCount(search, moods)
                        .let { friendsMapper.mapToFriends(it) }
                }

                getDaysUseCase
                    .getFilteredDaysFlow(search, moods)
                    .map {
                        insightsMapper.toInsightsState(
                            it,
                            search,
                            moods,
                            friendsDeferred.await()
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
}
