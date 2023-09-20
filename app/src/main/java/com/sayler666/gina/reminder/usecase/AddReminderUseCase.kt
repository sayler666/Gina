package com.sayler666.gina.reminder.usecase

import com.sayler666.gina.reminder.db.Reminder
import com.sayler666.gina.reminder.db.RemindersDatabaseProvider
import com.sayler666.gina.reminder.db.withRemindersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val setAlarmUseCase: SetAlarmUseCase,
    private val remindersDatabaseProvider: RemindersDatabaseProvider,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(reminder: Reminder) = withContext(coroutineDispatcher) {
        // store in database
        remindersDatabaseProvider.withRemindersDao { addReminder(reminder) }

        // set alarm
        setAlarmUseCase(reminder)
    }
}
