package com.sayler666.data.database.db.journal

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sayler666.data.database.db.journal.converter.DateConverter
import com.sayler666.data.database.db.journal.converter.MoodConverter
import com.sayler666.data.database.db.journal.converter.YearMonthConverter
import com.sayler666.data.database.db.journal.dao.DaysDao
import com.sayler666.data.database.db.journal.dao.RawDao
import com.sayler666.data.database.db.journal.entity.AttachmentEntity
import com.sayler666.data.database.db.journal.entity.DayEntity
import com.sayler666.data.database.db.journal.entity.DayFriendsEntity
import com.sayler666.data.database.db.journal.entity.FriendEntity


@Database(
    entities = [DayEntity::class, AttachmentEntity::class, FriendEntity::class, DayFriendsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MoodConverter::class, DateConverter::class, YearMonthConverter::class)
abstract class GinaDatabase : RoomDatabase() {
    abstract fun daysDao(): DaysDao

    abstract fun rawDao(): RawDao
}
