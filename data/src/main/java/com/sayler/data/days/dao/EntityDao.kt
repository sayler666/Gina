package com.sayler.data.days.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.sayler.data.days.entity.GinaEntity

interface EntityDao<in E : GinaEntity> {
    @Insert
    suspend fun insert(entity: E): Long

    @Insert
    suspend fun insertAll(vararg entity: E)

    @Insert
    suspend fun insertAll(entities: List<E>)

    @Update
    suspend fun update(entity: E)

    @Delete
    suspend fun delete(entity: E): Int

}
