package com.sayler666.gina.reminder.usecase

import com.sayler666.gina.reminder.db.Reminder
import com.sayler666.gina.reminder.viewmodel.Active
import com.sayler666.gina.reminder.viewmodel.NotActive
import com.sayler666.gina.reminder.viewmodel.ReminderEntity

fun ReminderEntity.toReminderOrNull(): Reminder? = when (this) {
    is Active -> Reminder(0, this.time)
    NotActive -> null
}

fun Reminder?.toReminderEntity(): ReminderEntity = when (this) {
    null -> NotActive
    else -> Active(this.time)
}
