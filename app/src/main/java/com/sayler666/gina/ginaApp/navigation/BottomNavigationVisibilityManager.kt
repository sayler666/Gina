package com.sayler666.gina.ginaApp.navigation

import com.sayler666.gina.ginaApp.navigation.BottomBarState.Hidden
import com.sayler666.gina.ginaApp.navigation.BottomBarState.Shown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BottomNavigationVisibilityManager @Inject constructor() {
    private var lock: AtomicBoolean = AtomicBoolean(false)

    private val mutableState = MutableStateFlow<BottomBarState>(Shown)
    val state = mutableState.asStateFlow()

    fun hide() {
        if (!lock.get()) mutableState.tryEmit(Hidden)
    }

    fun show() {
        if (!lock.get()) mutableState.tryEmit(Shown)
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

sealed interface BottomBarState {
    data object Shown : BottomBarState
    data object Hidden : BottomBarState
}
