package com.sayler666.gina.reminders.usecase

import com.sayler666.data.database.db.reminders.ReminderEntity
import com.sayler666.data.database.db.reminders.RemindersDatabaseProvider
import com.sayler666.data.database.db.reminders.withRemindersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val setAlarmUseCase: SetAlarmUseCase,
    private val remindersDatabaseProvider: RemindersDatabaseProvider,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(reminder: ReminderEntity) = withContext(coroutineDispatcher) {
        // store in database
        remindersDatabaseProvider.withRemindersDao { addReminder(reminder) }

        // set alarm
        setAlarmUseCase(reminder)
    }
}
