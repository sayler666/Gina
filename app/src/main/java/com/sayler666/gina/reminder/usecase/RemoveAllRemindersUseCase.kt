package com.sayler666.gina.reminder.usecase

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sayler666.gina.reminder.db.RemindersDatabaseProvider
import com.sayler666.gina.reminder.db.withRemindersDao
import com.sayler666.gina.reminder.receiver.ReminderReceiver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RemoveAllRemindersUseCase @Inject constructor(
    val context: Context,
    private val remindersDatabaseProvider: RemindersDatabaseProvider,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke() = withContext(coroutineDispatcher) {
        // remove reminders stored in database
        remindersDatabaseProvider.withRemindersDao { deleteAllReminders() }

        // remove alarms
        removeAlarms()
    }

    private fun removeAlarms() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        Timber.d("RemoveRemindersUseCase: remove all reminders (pending intents: $pendingIntent)")
        alarmManager.cancel(pendingIntent)
    }
}
