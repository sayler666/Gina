package com.sayler666.gina.reminder.usecase

import com.sayler666.domain.model.reminders.Reminder
import com.sayler666.gina.feature.settings.reminder.Active
import com.sayler666.gina.feature.settings.reminder.NotActive
import com.sayler666.gina.feature.settings.reminder.ReminderState

fun ReminderState.toReminderOrNull(): Reminder? = when (this) {
    is Active -> Reminder(0, this.time)
    NotActive -> null
}

fun Reminder?.toReminderState(): ReminderState = when (this) {
    null -> NotActive
    else -> Active(this.time)
}
