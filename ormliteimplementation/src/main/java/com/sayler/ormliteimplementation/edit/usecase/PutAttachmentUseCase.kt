package com.sayler.ormliteimplementation.edit.usecase

import com.annimon.stream.Stream
import com.sayler.gina.domain.IAttachment
import com.sayler.ormliteimplementation.AttachmentsDataProvider
import com.sayler.ormliteimplementation.entity.Attachment
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.OrmLiteErrorMapper
import io.reactivex.Observable

/**
 * Created by sayler on 26.01.2018.
 */
open class PutAttachmentUseCase(
        private val attachmentsDataProvider: AttachmentsDataProvider,
        private val ormLiteErrorMapper: OrmLiteErrorMapper) {
    open fun putAttachment(day: Day, attachments: List<IAttachment>): Observable<Int> {
        //remove old
        val deleteBuilder = attachmentsDataProvider.dao.deleteBuilder()
        deleteBuilder.where().eq(Attachment.DAYS_ID_COL, day.id)

        return Observable
                .just(deleteBuilder.delete())
                .doOnNext {
                    Stream.of(attachments)
                            .map { iAttachment -> iAttachment as Attachment }
                            .forEach {
                                it.day = day
                                attachmentsDataProvider.save(it)
                            }
                }.handleError()
    }

    private fun Observable<Int>.handleError(): Observable<Int> = onErrorResumeNext(::mapError)

    private fun mapError(error: Throwable): Observable<Int> {
        return Observable.error(ormLiteErrorMapper.map(error))
    }
}