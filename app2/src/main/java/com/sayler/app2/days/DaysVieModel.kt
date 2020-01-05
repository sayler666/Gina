package com.sayler.app2.days

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.sayler.app2.data.IDataManager
import com.sayler.app2.file.ActivityResultFlow
import com.sayler.app2.file.Result1
import com.sayler.app2.intent.Path.NotSet
import com.sayler.app2.intent.Path.Set
import com.sayler.app2.intent.getPath
import com.sayler.app2.mvrx.MvRxViewModel
import com.sayler.app2.mvrx.fragment
import com.sayler.data.days.entity.Day
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DaysState(
        val days: Async<List<Day>> = Uninitialized
) : MvRxState

@UseExperimental(InternalCoroutinesApi::class)
class DaysViewModel @AssistedInject constructor(
        @Assisted state: DaysState,
        private val dataManager: IDataManager,
        activityResult: ActivityResultFlow
) : MvRxViewModel<DaysState>(state) {

    init {
        observeRepositoriesChanges()
        observeDatabasePathChanges(activityResult)
    }

    private fun observeDatabasePathChanges(activityResult: ActivityResultFlow) = viewModelScope.launch {
        activityResult
                .observe(DaysFragment.REQUEST_CODE_SELECT_DB)
                .collect {
                    handleDbSelect(it)
                }
    }

    private fun handleDbSelect(it: Result1) {
        when (val path = it.data.getPath()) {
            is Set -> saveSettings(path())
            is NotSet -> Log.d("DaysViewModel", "Path not set (empty).")
        }
    }

    private fun saveSettings(databasePath: String) {
        dataManager.setSourceFile(databasePath)
        observeRepositoriesChanges()
    }

    fun addDay() = viewModelScope.launch {
        dataManager.dao { dayDao() }?.apply {
            insert(Day(date = Long.MAX_VALUE, content = "test " + Random.nextInt()))
        }
    }

    @ExperimentalCoroutinesApi
    private fun observeRepositoriesChanges() = viewModelScope.launch {
        dataManager.dao { dayDao() }?.apply {
            getAll()
                    .distinctUntilChanged()
                    .execute {
                        copy(days = it)
                    }
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: DaysState): DaysViewModel
    }

    companion object : MvRxViewModelFactory<DaysViewModel, DaysState> {

        const val TAG = "DaysViewModel"

        override fun create(viewModelContext: ViewModelContext, state: DaysState): DaysViewModel? {
            val fragment = viewModelContext.fragment<DaysFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }
}
