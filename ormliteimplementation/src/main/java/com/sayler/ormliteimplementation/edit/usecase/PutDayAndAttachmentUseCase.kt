package com.sayler.ormliteimplementation.edit.usecase

import com.sayler.monia.domain.IAttachment
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.OrmLiteErrorMapper
import io.reactivex.Observable

/**
 * Created by sayler on 26.01.2018.
 */
open class PutDayAndAttachmentUseCase(
        private val putAttachmentUseCase: PutAttachmentUseCase,
        private val putDayUseCase: PutDayUseCase,
        private val ormLiteErrorMapper: OrmLiteErrorMapper) {
    open fun put(day: Day, attachments: List<IAttachment>): Observable<Int> {
        return putDayUseCase
                .put(day)
                .flatMap {
                    putAttachmentUseCase.putAttachment(day, attachments)
                }
                .handleError()
    }

    private fun Observable<Int>.handleError(): Observable<Int> = onErrorResumeNext(::mapError)

    private fun mapError(error: Throwable): Observable<Int> {
        return Observable.error(ormLiteErrorMapper.map(error))
    }
}