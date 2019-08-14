package com.sayler.app2.ui.days

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.sayler.app2.data.IDataManager
import com.sayler.app2.file.OnActivityResultObserver
import com.sayler.app2.file.Result
import com.sayler.app2.intent.Path.NotSet
import com.sayler.app2.intent.Path.Set
import com.sayler.app2.intent.getPath
import com.sayler.app2.mvrx.MvRxViewModel
import com.sayler.data.days.entity.Attachment
import com.sayler.data.days.entity.Day
import com.sayler.data.settings.ISettingsRepository
import com.sayler.data.settings.SettingsData
import com.sayler.data.settings.SettingsState
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch


data class DaysState(
        val days: Async<List<Day>> = Uninitialized,
        val attachment: Async<List<Attachment>> = Uninitialized,
        val settingsState: SettingsState? = null
) : MvRxState

class DaysViewModel @AssistedInject constructor(
        @Assisted state: DaysState,
        private val dataManager: IDataManager,
        private val settingsRepository: ISettingsRepository,
        onActivityResultObserver: OnActivityResultObserver
) : MvRxViewModel<DaysState>(state) {

    init {
        observeFroRepositoriesChanges()

        setState {
            copy(settingsState = settingsRepository.get())
        }

        onActivityResultObserver
                .observe(DaysFragment.REQUEST_CODE_SELECT_DB)
                .distinctUntilChanged()
                .subscribe(::handleDbSelect)

    }

    private fun handleDbSelect(it: Result) {
        when (val a = it.data.getPath()) {
            is Set -> {
                Log.d("DaysViewModel", "Path set to ${a.path}")
                saveSettings(a.path)
            }
            is NotSet -> Log.d("DaysViewModel", "Path not set (empty).")
        }
    }

    fun saveSettings(databasePath: String) {
        settingsRepository.save(SettingsData(databasePath))
        dataManager.setSourceFile(databasePath)

        setState {
            copy(settingsState = settingsRepository.get())
        }

        observeFroRepositoriesChanges()
    }

    fun addDay() {
        viewModelScope.launch {
            dataManager.dao { dayDao() }
                    .insert(Day(content = "test", date = 1231231L))
                    .also {
                        dataManager.dao { attachmentDao() }.insert(Attachment(
                                dayId = it,
                                file = byteArrayOf(0x2E, 0x38),
                                mimeType = "img/jpg"))
                    }
        }
    }

    fun clearDays() {
        viewModelScope.launch {
            dataManager.dao { dayDao() }
                    .deleteAll()
        }
    }

    private fun observeFroRepositoriesChanges() {
        withState {
            dataManager.dao { dayDao() }
                    .getAll()
                    .distinctUntilChanged()
                    .execute {
                        copy(days = it)
                    }

            dataManager.dao { attachmentDao() }
                    .get(daysId = 2668)
                    .execute {
                        copy(attachment = it)
                    }
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
