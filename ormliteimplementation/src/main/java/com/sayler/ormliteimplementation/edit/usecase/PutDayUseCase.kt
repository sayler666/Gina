package com.sayler.ormliteimplementation.edit.usecase

import com.sayler.ormliteimplementation.DaysDataProvider
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.OrmLiteErrorMapper
import io.reactivex.Observable

/**
 * Created by sayler on 26.01.2018.
 */
open class PutDayUseCase(
        private val daysDataProvider: DaysDataProvider,
        private val ormLiteErrorMapper: OrmLiteErrorMapper) {
    open fun put(day: Day): Observable<Unit> {
        return Observable
                .just(daysDataProvider.save(day))
                .doOnNext { daysDataProvider.refresh(day) }
                .handleError()
    }

    private fun Observable<Unit>.handleError(): Observable<Unit> = onErrorResumeNext(::mapError)

    private fun mapError(error: Throwable): Observable<Unit> {
        return Observable.error(ormLiteErrorMapper.map(error))
    }
}