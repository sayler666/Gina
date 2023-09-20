package com.sayler666.gina.reminder.usecase

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sayler666.core.date.MILLIS_IN_DAY
import com.sayler666.gina.reminder.db.Reminder
import com.sayler666.gina.reminder.receiver.ReminderReceiver
import timber.log.Timber
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.inject.Inject

class SetAlarmUseCase @Inject constructor(
    val context: Context
) {
    operator fun invoke(reminder: Reminder) {
        Timber.d("SetAlarmUseCase: invoked")

        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val triggerTime =
                reminder.time.toEpochSecond(LocalDate.now(), OffsetDateTime.now().offset) * 1000
            Timber.d("SetAlarmUseCase: trigger at ${reminder.time}, every: $MILLIS_IN_DAY millis")

            // setting the alarm
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, triggerTime, MILLIS_IN_DAY, pendingIntent
            )

            // for notification testing
//            alarmManager.setExact(
//                AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
//            )
            Timber.d("SetAlarmUseCase: finished setting alarm")
        } catch (e: Exception) {
            Timber.d(e, "SetAlarmUseCase: Exception")
        }
    }

}
