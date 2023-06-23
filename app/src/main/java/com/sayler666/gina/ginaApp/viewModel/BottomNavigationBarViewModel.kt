package com.sayler666.gina.ginaApp.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class BottomNavigationBarViewModel @Inject constructor() : ViewModel() {
    val state = MutableStateFlow<BottomBarState>(BottomBarState.Shown)
    private var lock: AtomicBoolean = AtomicBoolean(false)
    fun hide() {
        if (!lock.get()) state.tryEmit(BottomBarState.Hidden)
    }

    fun show() {
        if (!lock.get()) state.tryEmit(BottomBarState.Shown)
    }

    fun lockHide() {
        hide()
        lock.set(true)
    }

    fun unlockAndShow() {
        lock.set(false)
        show()
    }
}

sealed class BottomBarState {
    object Shown : BottomBarState()
    object Hidden : BottomBarState()
}
