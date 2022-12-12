package com.sayler666.gina.core.flow

data class Event<T>(private var data: T?) {
    private var handled = false
    fun getValue(): T? {
        if (handled) return null
        handled = true
        return data
    }
}
