package com.sayler666.gina.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sayler666.gina.db.converter.DateConverter
import com.sayler666.gina.db.converter.MoodConverter
import com.sayler666.gina.db.dao.DaysDao
import com.sayler666.gina.db.dao.RawDao
import com.sayler666.gina.db.entity.Attachment
import com.sayler666.gina.db.entity.Day
import com.sayler666.gina.db.entity.DayFriends
import com.sayler666.gina.db.entity.Friend


@Database(
    entities = [Day::class, Attachment::class, Friend::class, DayFriends::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MoodConverter::class, DateConverter::class)
abstract class GinaDatabase : RoomDatabase() {
    abstract fun daysDao(): DaysDao

    abstract fun rawDao(): RawDao
}
