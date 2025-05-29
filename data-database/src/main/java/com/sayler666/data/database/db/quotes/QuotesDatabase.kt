package com.sayler666.data.database.db.quotes

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sayler666.data.database.db.journal.converter.DateConverter

@Database(
    entities = [QuoteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class QuotesDatabase : RoomDatabase() {
    abstract fun quotesDao(): QuotesDao
}


