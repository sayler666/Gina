package com.sayler666.gina.reminder.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sayler666.gina.reminder.usecase.RefreshAlarmsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RebootBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var refreshAlarmsUseCase: RefreshAlarmsUseCase

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, p1: Intent?) {
        Timber.d("RebootBroadcastReceiver", "BOOT_COMPLETED Received")
        CoroutineScope(Main).launch {
            refreshAlarmsUseCase()
        }
    }
}
