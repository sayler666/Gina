package com.sayler666.core.collections

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

fun <T> List<T>.mutate(block: (MutableList<T>) -> Unit): List<T> {
    val l = toMutableList()
    block(l)
    return l
}

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    mapNotNull { async { f(it) } }.awaitAll()
}
