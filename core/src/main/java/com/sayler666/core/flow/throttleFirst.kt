package com.sayler666.core.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.throttleFirst(windowMs: Long): Flow<T> = flow {
    var lastEmitTime = 0L
    collect { value ->
        val now = System.currentTimeMillis()
        if (now - lastEmitTime >= windowMs) {
            lastEmitTime = now
            emit(value)
        }
    }
}
