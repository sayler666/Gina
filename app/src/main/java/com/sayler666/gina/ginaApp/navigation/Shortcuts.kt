package com.sayler666.gina.ginaApp.navigation

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.sayler666.gina.ginaApp.ui.MainActivity
import com.sayler666.gina.navigation.ADD_DAY_URL
import com.sayler666.gina.resources.R

fun addDayShortcut(context: Context) = ShortcutInfoCompat.Builder(context, "AddDay")
    .setShortLabel("Add new entry")
    .setLongLabel("Add new entry")
    .setIcon(IconCompat.createWithResource(context, R.drawable.feather_icon_white))
    .setIntent(addDayDestinationIntent(context))
    .build()

fun addDayDestinationIntent(context: Context): Intent = Intent(
    ACTION_VIEW,
    ADD_DAY_URL.toUri(),
    context,
    MainActivity::class.java
)
