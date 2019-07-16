package com.sayler.ormliteimplementation.list.usecase

import com.sayler.ormliteimplementation.DaysDataProvider
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.OrmLiteErrorMapper
import io.reactivex.Observable

/**
 * Created by sayler on 2018-01-26.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
open class FindByTextUseCase(val daysDataProvider: DaysDataProvider, val ormLiteErrorMapper: OrmLiteErrorMapper) {
    open fun findByText(text: String): Observable<List<Day>> {
        val queryBuilder = daysDataProvider.dao.queryBuilder()
        val preparedQuery = queryBuilder.where().like(Day.CONTENT_COL, "%$text%").prepare()

        return Observable.just(daysDataProvider.dao.query(preparedQuery))
                .map {
                    it.sort()
                    it.reverse()
                    return@map it
                }
                .handleError()
    }

    private fun Observable<List<Day>>.handleError(): Observable<List<Day>> = onErrorResumeNext(::mapError)

    private fun mapError(error: Throwable): Observable<List<Day>> {
        return Observable.error(ormLiteErrorMapper.map(error))
    }
}