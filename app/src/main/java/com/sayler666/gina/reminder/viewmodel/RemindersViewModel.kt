package com.sayler666.gina.reminder.viewmodel

import com.sayler666.core.viewmodel.ViewModelSlice
import com.sayler666.gina.reminder.db.Reminder
import com.sayler666.gina.reminder.usecase.AddReminderUseCase
import com.sayler666.gina.reminder.usecase.GetLastReminderUseCase
import com.sayler666.gina.reminder.usecase.RemoveAllRemindersUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

interface RemindersViewModel : ViewModelSlice {
    val reminder: StateFlow<ReminderEntity>

    fun setReminder(localTime: LocalTime)

    fun removeReminders()
}

class RemindersViewModelImpl @Inject constructor(
    override var sliceScope: CoroutineScope,
    private val getLastReminderUseCase: GetLastReminderUseCase,
    private val addReminderUseCase: AddReminderUseCase,
    private val removeAllRemindersUseCase: RemoveAllRemindersUseCase
) : RemindersViewModel {
    private val _reminder: MutableStateFlow<ReminderEntity> = MutableStateFlow(NotActive)
    override val reminder: StateFlow<ReminderEntity> = _reminder.asStateFlow()

    init {
        sliceScope.launch {
            getLastReminderUseCase()
                .collect { _reminder.emit(it) }
        }
    }

    override fun setReminder(localTime: LocalTime) {
        sliceScope.launch {
            // remove old, only one reminder supported
            removeAllRemindersUseCase()

            // add new one
            addReminderUseCase(Reminder(time = localTime))
        }
    }

    override fun removeReminders() {
        sliceScope.launch {
            removeAllRemindersUseCase()
        }
    }
}
