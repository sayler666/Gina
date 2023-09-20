package com.sayler666.gina.reminder.receiver

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory.decodeResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.sayler666.gina.R
import com.sayler666.gina.ginaApp.MainActivity
import com.sayler666.gina.reminder.usecase.TodayEntryExistUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import timber.log.Timber
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
        createNotificationChannel(context)

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
        val notificationManager = NotificationManagerCompat.from(context)
        // Intent
        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapResultIntent,
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
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

    companion object {
        private const val REMINDERS_CHANNEL_ID = "reminders"
    }
}
