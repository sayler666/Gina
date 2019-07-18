package com.sayler.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.sayler.data.entity.GinaEntity

interface EntityDao<in E : GinaEntity> {
    @Insert
    fun insert(entity: E): Long

    @Insert
    fun insertAll(vararg entity: E)

    @Insert
    fun insertAll(entities: List<E>)

    @Update
    fun update(entity: E)

    @Delete
    fun delete(entity: E): Int
}
