package com.sayler666.gina.reminder.viewmodel

import com.sayler666.data.database.db.reminders.ReminderEntity
import com.sayler666.gina.feature.settings.reminder.NotActive
import com.sayler666.gina.feature.settings.reminder.ReminderState
import com.sayler666.gina.feature.settings.reminder.RemindersViewModel
import com.sayler666.gina.reminder.usecase.AddReminderUseCase
import com.sayler666.gina.reminder.usecase.GetLastReminderUseCase
import com.sayler666.gina.reminder.usecase.RemoveAllRemindersUseCase
import com.sayler666.gina.reminder.usecase.toReminderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

class RemindersViewModelImpl @Inject constructor(
    override var sliceScope: CoroutineScope,
    private val getLastReminderUseCase: GetLastReminderUseCase,
    private val addReminderUseCase: AddReminderUseCase,
    private val removeAllRemindersUseCase: RemoveAllRemindersUseCase
) : RemindersViewModel {
    private val _reminder: MutableStateFlow<ReminderState> = MutableStateFlow(NotActive)
    override val reminder: StateFlow<ReminderState> = _reminder.asStateFlow()

    init {
        sliceScope.launch {
            getLastReminderUseCase()
                .collect { _reminder.emit(it.toReminderState()) }
        }
    }

    override fun setReminder(localTime: LocalTime) {
        sliceScope.launch {
            // remove old, only one reminder supported
            removeAllRemindersUseCase()

            // add new one
            addReminderUseCase(ReminderEntity(time = localTime))
        }
    }

    override fun removeReminders() {
        sliceScope.launch {
            removeAllRemindersUseCase()
        }
    }
}
