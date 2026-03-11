package com.sayler666.gina.reminders.usecase

import com.sayler666.domain.model.reminders.Reminder
import com.sayler666.gina.reminders.viewmodel.Active
import com.sayler666.gina.reminders.viewmodel.NotActive
import com.sayler666.gina.reminders.viewmodel.ReminderState

fun ReminderState.toReminderOrNull(): Reminder? = when (this) {
    is Active -> Reminder(0, this.time)
    NotActive -> null
}

fun Reminder?.toReminderState(): ReminderState = when (this) {
    null -> NotActive
    else -> Active(this.time)
}
