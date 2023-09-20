package com.sayler666.gina.reminder.db.converter

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeConverter {

    @TypeConverter
    fun toLocalTime(time: String): LocalTime =
        LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME)

    @TypeConverter
    fun fromMoodToInt(localTime: LocalTime): String =
        DateTimeFormatter.ISO_LOCAL_TIME.format(localTime)

}
