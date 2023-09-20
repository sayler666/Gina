package com.sayler666.gina.reminder.viewmodel

import java.time.LocalTime

sealed interface ReminderEntity
data object NotActive : ReminderEntity
data class Active(val time: LocalTime) : ReminderEntity
