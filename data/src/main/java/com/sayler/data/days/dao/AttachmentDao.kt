package com.sayler.data.days.dao

import androidx.room.Dao
import androidx.room.Query
import com.sayler.data.days.entity.Attachment
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
abstract class AttachmentDao : EntityDao<Attachment> {
    @Query("SELECT * FROM attachments")
    abstract fun getAll(): Observable<List<Attachment>>

    @Query("SELECT * FROM attachments WHERE days_id = :daysId")
    abstract fun get(daysId: Long): Observable<List<Attachment>>

}
