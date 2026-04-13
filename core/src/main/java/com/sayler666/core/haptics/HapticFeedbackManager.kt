package com.sayler666.core.haptics

interface HapticFeedbackManager {
    fun tap()
    fun swipe()
    fun newDayAdded()
    fun dayRemoved()
    fun toggle(boolean: Boolean)
}
