package com.sayler666.gina.ginaApp.navigation

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.sayler666.gina.R
import com.sayler666.gina.addDay.ui.ADD_DAY_URL

fun addDayShortcut(context: Context) = ShortcutInfoCompat.Builder(context, "AddDay")
    .setShortLabel("Add new entry")
    .setLongLabel("Add new entry")
    .setIcon(IconCompat.createWithResource(context, R.drawable.feather_icon_white))
    .setIntent(addDayDestinationIntent())
    .build()

fun addDayDestinationPendingIntent(context: Context): PendingIntent =
    TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(addDayDestinationIntent())
        getPendingIntent(1, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
    }

fun addDayDestinationIntent(): Intent = Intent(
    ACTION_VIEW,
    ADD_DAY_URL.toUri()
)
