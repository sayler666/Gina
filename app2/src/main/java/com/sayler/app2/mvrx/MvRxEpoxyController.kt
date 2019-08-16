package com.sayler.app2.mvrx

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.withState

class MvRxEpoxyController(
        val buildModelsCallback: MvRxEpoxyController.() -> Unit
) : AsyncEpoxyController() {
    override fun buildModels() {
        buildModelsCallback()
    }
}

fun <S : MvRxState, VM : MvRxViewModel<S>> MvRxFragment.viewModelController(
        viewModel: VM,
        buildModels: MvRxEpoxyController.(state: S) -> Unit
) = MvRxEpoxyController {
    if (view == null || isRemoving) return@MvRxEpoxyController
    withState(viewModel) { state ->
        buildModels(state)
    }
}
