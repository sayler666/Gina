package com.sayler666.data.database.db.journal

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sayler666.data.database.db.journal.converter.DateConverter
import com.sayler666.data.database.db.journal.converter.MoodConverter
import com.sayler666.data.database.db.journal.converter.YearMonthConverter
import com.sayler666.data.database.db.journal.dao.DaysDao
import com.sayler666.data.database.db.journal.dao.RawDao
import com.sayler666.data.database.db.journal.entity.AttachmentEntity
import com.sayler666.data.database.db.journal.entity.DayEntity
import com.sayler666.data.database.db.journal.entity.DayFriendsEntity
import com.sayler666.data.database.db.journal.entity.FriendEntity
import com.sayler666.core.string.decodeHtmlEntitiesPreservingTags


@Database(
    entities = [DayEntity::class, AttachmentEntity::class, FriendEntity::class, DayFriendsEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(MoodConverter::class, DateConverter::class, YearMonthConverter::class)
abstract class GinaDatabase : RoomDatabase() {
    abstract fun daysDao(): DaysDao

    abstract fun rawDao(): RawDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE attachments ADD COLUMN hidden INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.query("SELECT id, content FROM days").use { cursor ->
                    val idIdx = cursor.getColumnIndexOrThrow("id")
                    val contentIdx = cursor.getColumnIndexOrThrow("content")
                    while (cursor.moveToNext()) {
                        if (cursor.isNull(contentIdx)) continue
                        val id = cursor.getLong(idIdx)
                        val original = cursor.getString(contentIdx)
                        val decoded = original.decodeHtmlEntitiesPreservingTags()
                        if (decoded != original) {
                            db.execSQL(
                                "UPDATE days SET content = ? WHERE id = ?",
                                arrayOf<Any>(decoded, id)
                            )
                        }
                    }
                }
            }
        }
    }
}
