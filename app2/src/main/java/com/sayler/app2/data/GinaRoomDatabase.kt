package com.sayler.app2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sayler.data.GinaDatabase
import com.sayler.data.entity.Day

@Database(entities = arrayOf(Day::class), version = 1)
abstract class GinaRoomDatabase : RoomDatabase(), GinaDatabase
