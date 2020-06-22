package com.sayler.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.sayler.data.entity.Attachment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AttachmentDao : EntityDao<Attachment> {
    @Query("SELECT * FROM attachments")
    abstract fun getAll(): Flow<List<Attachment>>

    @Query("SELECT * FROM attachments WHERE days_id = :daysId")
    abstract fun get(daysId: Long): Flow<List<Attachment>>

}