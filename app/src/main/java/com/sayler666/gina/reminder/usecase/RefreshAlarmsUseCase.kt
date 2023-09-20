package com.sayler666.gina.reminder.usecase

import com.sayler666.gina.reminder.db.RemindersDatabaseProvider
import com.sayler666.gina.reminder.db.withRemindersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RefreshAlarmsUseCase @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val remindersDatabaseProvider: RemindersDatabaseProvider,
    private val setAlarmUseCase: SetAlarmUseCase
) {
    suspend operator fun invoke() = withContext(coroutineDispatcher) {
        remindersDatabaseProvider.withRemindersDao {
            getAllReminders().forEach {
                Timber.d("RefreshAlarmsUseCase: set Alarm [time: ${it.time}, id: ${it.id}]")
                setAlarmUseCase(it)
            }
        }
    }

}
