package com.sayler.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sayler.data.dao.AttachmentDao
import com.sayler.data.dao.DayDao
import com.sayler.data.entity.Attachment
import com.sayler.data.entity.Day

interface GinaDatabase {
    fun dayDao(): DayDao
    fun attachmentDao(): AttachmentDao
    fun closeConnection()
}

@Database(entities = arrayOf(Day::class, Attachment::class), version = 1)
abstract class GinaDatabaseRoomImpl : RoomDatabase(), GinaDatabase {
    override fun closeConnection() = close()
}
