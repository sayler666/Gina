package com.sayler.app2.days

import android.util.Log
import com.airbnb.mvrx.*
import com.sayler.app2.data.IDataManager
import com.sayler.app2.file.OnActivityResultObserver
import com.sayler.app2.file.Result
import com.sayler.app2.intent.Path.NotSet
import com.sayler.app2.intent.Path.Set
import com.sayler.app2.intent.getPath
import com.sayler.app2.mvrx.MvRxViewModel
import com.sayler.app2.mvrx.fragment
import com.sayler.data.days.entity.Day
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable

data class DaysState(
        val days: Async<List<Day>> = Uninitialized
) : MvRxState

class DaysViewModel @AssistedInject constructor(
        @Assisted state: DaysState,
        private val dataManager: IDataManager,
        onActivityResultObserver: OnActivityResultObserver
) : MvRxViewModel<DaysState>(state) {

    init {
        observeFroRepositoriesChanges()

        withState {
            onActivityResultObserver
                    .observe(DaysFragment.REQUEST_CODE_SELECT_DB)
                    .distinctUntilChanged()
                    .subscribe(::handleDbSelect)
        }
    }

    private fun handleDbSelect(it: Result) {
        when (val path = it.data.getPath()) {
            is Set -> saveSettings(path())
            is NotSet -> Log.d("DaysViewModel", "Path not set (empty).")
        }
    }

    fun saveSettings(databasePath: String) {
        dataManager.setSourceFile(databasePath)
        observeFroRepositoriesChanges()
    }

    fun addDay() {
        // TODO navigate to add day screen
    }

    private fun observeFroRepositoriesChanges() {
        withState {
            dataManager.dao { dayDao() }?.apply {
                getAll()
                        .distinctUntilChanged()
                        .onErrorResumeNext { t: Throwable ->
                            Log.e(TAG, t.message, t.cause)
                            Observable.just(emptyList())
                        }
                        .execute {
                            copy(days = it)
                        }
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
