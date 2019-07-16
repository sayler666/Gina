package com.sayler.ormliteimplementation.day.usecase

import com.sayler.ormliteimplementation.DaysDataProvider
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.OrmLiteErrorMapper
import io.reactivex.Observable

/**
 * Created by sayler on 26.01.2018.
 */
open class FindDayByIdUseCase(val daysDataProvider: DaysDataProvider, val ormLiteErrorMapper: OrmLiteErrorMapper) {
    open fun findDayById(id: Long): Observable<Day> {
        return Observable.just(daysDataProvider.get(id)).handleError()
    }

    private fun Observable<Day>.handleError(): Observable<Day> = onErrorResumeNext(::mapError)

    private fun mapError(error: Throwable): Observable<Day> {
        return Observable.error(ormLiteErrorMapper.map(error))
    }
}