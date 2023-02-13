package com.sayler666.gina.core.list

fun <T> List<T>.mutate(block: (MutableList<T>) -> Unit): List<T> {
    val l = toMutableList()
    block(l)
    return l
}
