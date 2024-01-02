package com.sayler666.gina.reminder.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sayler666.core.date.MILLIS_IN_DAY
import com.sayler666.gina.reminder.usecase.NotificationUseCase
import com.sayler666.gina.reminder.usecase.TodayEntryExistUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject


@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var todayEntryExistUseCase: TodayEntryExistUseCase

    @Inject
    lateinit var notificationUseCase: NotificationUseCase


    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("ReminderReceiver: onReceive")
        scheduleNextAlarm(context)

        CoroutineScope(Main).launch {
            if (todayEntryExistUseCase().not()) {
                Timber.d("ReminderReceiver: no entry today, showing notification")
                createNotificationChannel(context)
                createNotification()
            } else {
                Timber.d("ReminderReceiver: entry exists.")
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        notificationUseCase.createNotificationChannel(
            context = context,
            channelId = REMINDERS_CHANNEL_ID,
            name = "Reminders",
            desc = "Reminders channel"
        )
    }

    private fun createNotification() {
        notificationUseCase.showNotification(
            title = "Hey, Gina here",
            content = "Don't forget to write something today!",
            notificationId = REMINDER_NOTIFICATION_ID,
            notificationChannel = REMINDERS_CHANNEL_ID
        )
    }

    private fun scheduleNextAlarm(context: Context, fromNow: Long = MILLIS_IN_DAY) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, ReminderReceiver::class.java),
                FLAG_UPDATE_CURRENT or FLAG_MUTABLE
            )

            val triggerTimeEpochMillis = LocalTime.now()
                .toEpochSecond(LocalDate.now(), OffsetDateTime.now().offset) * 1000 + fromNow

            if (alarmManager.canScheduleExactAlarms())
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerTimeEpochMillis, pendingIntent
                )

            val alarmTime = Instant.ofEpochSecond(triggerTimeEpochMillis / 1000)
                .atZone(ZoneId.systemDefault())
            Timber.d("ReminderReceiver: next alarm at $alarmTime")
        } catch (e: Exception) {
            Timber.d(e, "ReminderReceiver: Exception scheduling next alarm")
        }
    }

    companion object {
        const val REMINDER_NOTIFICATION_ID = 1
        const val REMINDERS_CHANNEL_ID = "reminders"
    }

}
