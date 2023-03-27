package com.sayler666.gina.core.flow

import com.sayler666.gina.core.flow.Event.Value

sealed class Event<out T : Any> {

    data class Value<out T : Any>(private var data: T?) : Event<T>() {
        private var handled = false
        fun getValue(): T? {
            if (handled) return null
            handled = true
            return data
        }
    }

    object Empty : Event<Nothing>()
}

fun <T : Any> Event<T>.withValue(block: (T) -> Unit) {
    if (this is Value) getValue()?.let { block(it) }
}
