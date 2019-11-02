package com.sayler.data.days.dao

import androidx.room.Dao
import androidx.room.Query
import com.sayler.data.days.entity.Day
import io.reactivex.Observable

@Dao
abstract class DayDao : EntityDao<Day> {
    @Query("SELECT * FROM days ORDER BY id DESC")
    abstract fun getAll(): Observable<List<Day>>

    @Query("SELECT * FROM days WHERE id = :id")
    abstract fun get(id: Long): Observable<Day>

    @Query("DELETE FROM days")
    suspend abstract fun deleteAll()
}
