package com.sayler666.gina.reminder.usecase

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import com.sayler666.core.date.MILLIS_IN_DAY
import com.sayler666.gina.reminder.db.Reminder
import com.sayler666.gina.reminder.receiver.ReminderReceiver
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
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
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
                )

            var triggerTimeEpochMillis =
                reminder.time.toEpochSecond(LocalDate.now(), OffsetDateTime.now().offset) * 1000
            if (reminder.time < LocalTime.now()) triggerTimeEpochMillis += MILLIS_IN_DAY

            val alarmTime = Instant.ofEpochSecond(triggerTimeEpochMillis / 1000)
                .atZone(ZoneId.systemDefault())

            Timber.d("SetAlarmUseCase: trigger at $alarmTime")

            if (alarmManager.canScheduleExactAlarms())
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerTimeEpochMillis, pendingIntent
                )
            Timber.d("SetAlarmUseCase: finished setting alarm")
        } catch (e: Exception) {
            Timber.d(e, "SetAlarmUseCase: Exception")
        }
    }

}
