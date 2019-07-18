package com.sayler.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.sayler.data.entity.Day
import io.reactivex.Observable

@Dao
abstract class DayDao : EntityDao<Day> {
    @Query("SELECT * FROM days")
    abstract fun getAll(): Observable<List<Day>>
}
