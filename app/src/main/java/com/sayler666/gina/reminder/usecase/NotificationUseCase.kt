package com.sayler666.gina.reminder.usecase

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory.decodeResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.sayler666.gina.R
import com.sayler666.gina.ginaApp.navigation.addDayDestinationPendingIntent
import com.sayler666.gina.reminder.receiver.ReminderReceiver.Companion.REMINDERS_CHANNEL_ID
import timber.log.Timber
import javax.inject.Inject

interface NotificationUseCase {
    fun createNotificationChannel(
        context: Context, channelId: String,
        name: String,
        desc: String
    )

    fun showNotification(
        title: String,
        content: String,
        notificationId: Int,
        notificationChannel: String
    )

    fun hideNotificationById(notificationId: Int)
}

class NotificationUseCaseImpl @Inject constructor(
    val context: Context
) : NotificationUseCase {
    override fun showNotification(
        title: String,
        content: String,
        notificationId: Int,
        notificationChannel: String
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        // Intent
        val pendingIntent = createMainActivityIntent(context)
        // Notification
        val notification = NotificationCompat.Builder(context, REMINDERS_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification_monochrome)
            .setLargeIcon(decodeResource(context.resources, R.drawable.ic_notification_monochrome))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent).build()

        if (checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            notificationManager.notify(notificationId, notification)
            Timber.d("ReminderReceiver: show notification")
        } else {
            Timber.d("ReminderReceiver: missing POST_NOTIFICATIONS permission")
        }
    }

    override fun createNotificationChannel(
        context: Context,
        channelId: String,
        name: String,
        desc: String
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = desc
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createMainActivityIntent(context: Context): PendingIntent? =
        addDayDestinationPendingIntent(context)

    override fun hideNotificationById(notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }

}
