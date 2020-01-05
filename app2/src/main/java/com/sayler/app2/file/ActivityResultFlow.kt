package com.sayler.app2.file

import android.content.Intent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import javax.inject.Singleton


data class Result1(val requestCode: Int, val resultCode: Int, val data: Intent?)

@Singleton
class ActivityResultFlow @Inject constructor() {

    @ExperimentalCoroutinesApi
    private val channel = ConflatedBroadcastChannel<Result1>()

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun observe(requestCode: Int): Flow<Result1> = channel
            .asFlow()
            .filter {
                it.requestCode == requestCode
            }

    @ExperimentalCoroutinesApi
    suspend fun send(requestCode: Int, resultCode: Int, data: Intent?) {
        channel.send(Result1(requestCode, resultCode, data))
    }

}
