package com.sayler.ormliteimplementation.edit.usecase

import com.sayler.ormliteimplementation.DaysDataProvider
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.OrmLiteErrorMapper
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * Created by sayler on 26.01.2018.
 */
open class DeleteDayUseCase(val daysDataProvider: DaysDataProvider, val ormLiteErrorMapper: OrmLiteErrorMapper) {
    open fun delete(id: Long): Observable<Unit> {
        return Observable.just(daysDataProvider.delete(id)).handleError()
    }

    private fun Observable<Unit>.handleError(): Observable<Unit> = onErrorResumeNext(::mapError)

    private fun mapError(error: Throwable): Observable<Unit>  {
        return Observable.error(ormLiteErrorMapper.map(error))
    }
}