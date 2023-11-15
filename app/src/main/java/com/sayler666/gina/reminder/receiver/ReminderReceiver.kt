package com.sayler666.gina.reminder.receiver

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory.decodeResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.sayler666.core.date.MILLIS_IN_DAY
import com.sayler666.gina.R
import com.sayler666.gina.ginaApp.MainActivity
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

    private fun createNotificationChannel(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(REMINDERS_CHANNEL_ID, "Reminder Channel", importance).apply {
                description = "Notification for reminders"
            }
        notificationManager.createNotificationChannel(channel)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("ReminderReceiver: onReceive")
        scheduleNextAlarm(context)

        CoroutineScope(Main).launch {
            if (todayEntryExistUseCase().not()) {
                Timber.d("ReminderReceiver: no entry today, showing notification")
                createNotification(context)
            } else {
                Timber.d("ReminderReceiver: entry exists.")
            }
        }
    }

    private fun createNotification(context: Context) {
        createNotificationChannel(context)

        val notificationManager = NotificationManagerCompat.from(context)
        // Intent
        val pendingIntent = createMainActivityIntent(context)
        // Notification
        val notification = NotificationCompat.Builder(context, REMINDERS_CHANNEL_ID)
            .setContentTitle("Hey, Gina here")
            .setContentText("Don't forget to write something today!")
            .setSmallIcon(R.drawable.ic_notification_monochrome)
            .setLargeIcon(decodeResource(context.resources, R.drawable.ic_notification_monochrome))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        if (checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            notificationManager.notify(1, notification)
            Timber.d("ReminderReceiver: show notification")
        } else {
            Timber.d("ReminderReceiver: missing POST_NOTIFICATIONS permission")
        }
    }

    private fun createMainActivityIntent(context: Context): PendingIntent? =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply { flags = FLAG_ACTIVITY_SINGLE_TOP },
            FLAG_UPDATE_CURRENT or FLAG_MUTABLE
        )

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
        private const val REMINDERS_CHANNEL_ID = "reminders"
    }
}
