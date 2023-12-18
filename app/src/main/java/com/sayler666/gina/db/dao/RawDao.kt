package com.sayler666.gina.db.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface RawDao {
    @RawQuery
    suspend fun raw(supportSQLiteQuery: SupportSQLiteQuery): Int

    suspend fun vacuum() = raw(SimpleSQLiteQuery("VACUUM"))
}
