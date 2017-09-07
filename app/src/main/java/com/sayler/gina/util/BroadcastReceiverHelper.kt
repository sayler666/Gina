package com.sayler.gina.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BroadcastReceiverHelper
/**
 * @param action action to perform after broadcast gets received
 */
constructor(val action: () -> Unit) {
    var intent: Intent? = null
        private set
    private val broadcastReceiver: BroadcastReceiver
    private var needToRunAction = false

    init {
        broadcastReceiver = BroadcastReceiverHelper.onReceive { _, intent ->
            needToRunAction = true
            this.intent = intent
        }
    }

    /**
     * @return true if action was called
     * * best to call this method in onResume
     */
    fun callScheduledAction(): Boolean {
        if (needToRunAction) {
            needToRunAction = false
            action()
            return true
        }
        return false
    }

    fun register(context: Context, intentFilter: IntentFilter) {
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(broadcastReceiver)
    }

    companion object {
        fun onReceive(action2: (Context, Intent) -> Unit): BroadcastReceiver {
            return object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    action2(context, intent)
                }
            }
        }
    }

}