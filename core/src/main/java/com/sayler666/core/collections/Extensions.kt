package com.sayler666.core.collections

fun <T> List<T>.mutate(block: (MutableList<T>) -> Unit): List<T> {
    val l = toMutableList()
    block(l)
    return l
}
