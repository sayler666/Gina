package com.sayler.app2.ui.days

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.sayler.app2.data.IDataManager
import com.sayler.app2.mvrx.MvRxViewModel
import com.sayler.data.days.entity.Day
import com.sayler.data.settings.ISettingsRepository
import com.sayler.data.settings.SettingsData
import com.sayler.data.settings.SettingsState
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch


data class DaysState(
        val days: Async<List<Any>> = Uninitialized,
        val settingsState: SettingsState? = null
) : MvRxState

class DaysViewModel @AssistedInject constructor(
        @Assisted state: DaysState,
        private val dataManager: IDataManager,
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

        setState {
            copy(settingsState = settingsRepository.get())
        }
    }

    fun saveSettings(databasePath: String) {
        settingsRepository.save(SettingsData(databasePath))
        dataManager.setSourceFile(databasePath)

        setState {
            copy(settingsState = settingsRepository.get())
        }
    }

    fun addDay() {
        viewModelScope.launch {
            dataManager.dao { dayDao() }
                    .insert(Day(content = "test", date = 1231231L))
        }
    }

    fun clearDays() {
        viewModelScope.launch {
            dataManager.dao { dayDao() }
                    .deleteAll()
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
