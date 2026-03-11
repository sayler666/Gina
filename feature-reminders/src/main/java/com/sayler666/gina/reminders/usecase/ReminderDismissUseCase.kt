package com.sayler666.gina.reminders.usecase

import com.sayler666.gina.reminders.receiver.ReminderReceiver.Companion.REMINDER_NOTIFICATION_ID
import javax.inject.Inject

interface ReminderDismissUseCase {
    fun dismissReminderNotification()
}

class ReminderDismissUseCaseImpl @Inject constructor(
    private val notificationUseCase: NotificationUseCase
) : ReminderDismissUseCase {
    override fun dismissReminderNotification() {
        notificationUseCase.hideNotificationById(REMINDER_NOTIFICATION_ID)
    }
}
