package com.sayler.app2.ui.days

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.sayler.app2.data.DataManager
import com.sayler.app2.mvrx.MvRxViewModel
import com.sayler.data.days.entity.Day
import com.sayler.data.settings.ISettingsRepository
import com.sayler.data.settings.SettingsData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch


data class DaysState(val days: Async<List<Any>> = Uninitialized) : MvRxState

class DaysViewModel @AssistedInject constructor(
        @Assisted state: DaysState,
        private val dataManager: DataManager,
        private val settingsRepository: ISettingsRepository
) : MvRxViewModel<DaysState>(state) {

    init {
        withState {
            dataManager.dao { dayDao() }
                    .getAll()
                    .distinctUntilChanged()
                    .execute {
                        copy(days = it)
                    }
        }
    }

    fun saveSettings(databasePath: String) {
        settingsRepository.save(SettingsData(databasePath))
        dataManager.setSourceFile(databasePath)

        readDays()
    }

    fun readSettings() {
        val settingsState = settingsRepository.get()
        Log.d("DaysViewModel", settingsState.toString())
    }

    fun readDays() {
        withState {
            dataManager.dao { dayDao() }
                    .getAll().execute {
                        copy(days = it)
                    }
        }
    }

    fun addDay() {
        viewModelScope.launch {
            dataManager.dao { dayDao() }
                    .insert(Day(content = "test", date = 1231231L))
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: DaysState): DaysViewModel
    }

    companion object : MvRxViewModelFactory<DaysViewModel, DaysState> {
        override fun create(viewModelContext: ViewModelContext, state: DaysState): DaysViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<DaysFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }
}
