package com.sayler666.gina.feature.settings.reminder

import java.time.LocalTime

sealed interface ReminderState
data object NotActive : ReminderState
data class Active(val time: LocalTime) : ReminderState
