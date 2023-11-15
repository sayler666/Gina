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
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("RebootBroadcastReceiver", "Received")
        intent?.action?.let { action ->
            Timber.d("RebootBroadcastReceiver", "$action received")
        }
        CoroutineScope(Main).launch {
            refreshAlarmsUseCase()
        }
    }
}
