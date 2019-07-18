package com.sayler.app2.ui.days

import com.airbnb.mvrx.*
import com.sayler.app2.mvrx.MvRxViewModel
import com.sayler.data.dao.DayDao
import com.sayler.data.entity.Day
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject


data class DaysState(val days: Async<List<Any>> = Uninitialized) : MvRxState

class DaysViewModel @AssistedInject constructor(
        @Assisted state: DaysState,
        dayDao: DayDao
) : MvRxViewModel<DaysState>(state) {

    init {
        withState {
            dayDao.getAll().execute {
                copy(days = it)
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
