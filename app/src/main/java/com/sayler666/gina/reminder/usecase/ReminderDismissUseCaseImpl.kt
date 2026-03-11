package com.sayler666.gina.reminder.usecase

import com.sayler666.gina.day.addDay.usecase.ReminderDismissUseCase
import com.sayler666.gina.reminder.receiver.ReminderReceiver.Companion.REMINDER_NOTIFICATION_ID
import javax.inject.Inject

class ReminderDismissUseCaseImpl @Inject constructor(
    private val notificationUseCase: NotificationUseCase
) : ReminderDismissUseCase {
    override fun dismissReminderNotification() {
        notificationUseCase.hideNotificationById(REMINDER_NOTIFICATION_ID)
    }
}
