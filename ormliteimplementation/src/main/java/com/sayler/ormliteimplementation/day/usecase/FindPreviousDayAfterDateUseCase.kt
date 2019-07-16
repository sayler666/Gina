package com.sayler.ormliteimplementation.day.usecase

import com.sayler.ormliteimplementation.DaysDataProvider
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.OrmLiteErrorMapper
import io.reactivex.Observable
import org.joda.time.DateTime

/**
 * Created by sayler on 26.01.2018.
 */
open class FindPreviousDayAfterDateUseCase(val daysDataProvider: DaysDataProvider, val ormLiteErrorMapper: OrmLiteErrorMapper) {
    open fun findPreviousDayAfterDateId(dateTime: DateTime): Observable<Day> {
        val queryBuilder = daysDataProvider.dao.queryBuilder()
        val preparedQuery = queryBuilder
                .orderBy(Day.DATE_COL, false)
                .limit(1L).
                where().lt(Day.DATE_COL, dateTime)
                .prepare()

        return Observable.just(daysDataProvider.dao.query(preparedQuery))
                .map { it[0] }
                .handleError()
    }

    private fun Observable<Day>.handleError(): Observable<Day> = onErrorResumeNext(::mapError)

    private fun mapError(error: Throwable): Observable<Day> {
        return Observable.error(ormLiteErrorMapper.map(error))
    }
}