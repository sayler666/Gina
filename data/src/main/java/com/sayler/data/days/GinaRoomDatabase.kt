package com.sayler.data.days

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sayler.data.days.entity.Day

@Database(entities = arrayOf(Day::class), version = 1)
abstract class GinaRoomDatabase : RoomDatabase(), GinaDatabase