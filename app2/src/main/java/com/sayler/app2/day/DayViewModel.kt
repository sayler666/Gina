package com.sayler.app2.day

import com.airbnb.mvrx.*
import com.sayler.app2.data.IDataManager
import com.sayler.app2.mvrx.MvRxViewModel
import com.sayler.app2.mvrx.fragment
import com.sayler.data.days.entity.Day
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

data class DayState(
        val day: Async<Day> = Uninitialized,
        val dayId: Long
) : MvRxState


class DayViewModel @AssistedInject constructor(
        @Assisted state: DayState,
        private val dataManager: IDataManager
) : MvRxViewModel<DayState>(state) {

    init {
        withState {
            dataManager.dao { dayDao() }?.apply {
                get(state.dayId)
                        .distinctUntilChanged()
                        .execute {
                            copy(day = it)
                        }
            }
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: DayState): DayViewModel
    }

    companion object : MvRxViewModelFactory<DayViewModel, DayState> {

        const val TAG = "DaysViewModel"

        override fun create(viewModelContext: ViewModelContext, state: DayState): DayViewModel? {
            val fragment = viewModelContext.fragment<DayFragment>()
            return fragment.viewModelFactory.create(state)
        }

        override fun initialState(viewModelContext: ViewModelContext): DayState? {
            val fragment = viewModelContext.fragment<DayFragment>()
            return DayState(dayId = fragment.arguments.dayId)
        }
    }

}
