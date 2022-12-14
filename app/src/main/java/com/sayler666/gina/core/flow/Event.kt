package com.sayler666.gina.core.flow

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
