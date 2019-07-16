package com.sayler.app2.ui.days

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.sayler.app2.mvrx.MvRxViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject


data class DaysState(val days: List<Any> = emptyList()) : MvRxState

class DaysViewModel @AssistedInject constructor(
        @Assisted state: DaysState
) : MvRxViewModel<DaysState>(state) {


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
