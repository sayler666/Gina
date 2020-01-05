package com.sayler.app2.mvrx

import com.airbnb.mvrx.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

abstract class MvRxViewModel<S : MvRxState>(initialState: S) :
        BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG) {

    protected suspend inline fun <T> Flow<T>.execute(
            crossinline stateReducer: S.(Async<T>) -> S
    ) = execute({ it }, stateReducer)

    protected suspend inline fun <T, V> Flow<T>.execute(
            crossinline mapper: (T) -> V,
            crossinline stateReducer: S.(Async<V>) -> S
    ) {
        setState { stateReducer(Loading()) }

        return map { Success(mapper(it)) as Async<V> }
                .catch { emit(Fail(it)) }
                .collect { setState { stateReducer(it) } }
    }
}
