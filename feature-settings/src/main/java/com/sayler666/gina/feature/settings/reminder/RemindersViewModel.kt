package com.sayler666.gina.feature.settings.reminder

import com.sayler666.core.viewmodel.ViewModelSlice
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime

interface RemindersViewModel : ViewModelSlice {
    val reminder: StateFlow<ReminderState>

    fun setReminder(localTime: LocalTime)

    fun removeReminders()
}
