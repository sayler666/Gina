package com.sayler666.gina.reminder.viewmodel

import java.time.LocalTime

sealed interface ReminderState
data object NotActive : ReminderState
data class Active(val time: LocalTime) : ReminderState
