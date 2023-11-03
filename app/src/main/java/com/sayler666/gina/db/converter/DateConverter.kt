package com.sayler666.gina.db.converter

import androidx.room.TypeConverter
import com.sayler666.core.date.MILLIS_IN_DAY
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DateConverter {

    @TypeConverter
    fun toLocalDate(timestamp: Long): LocalDate = Instant.ofEpochSecond(timestamp / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    @TypeConverter
    fun toTimestamp(localDate: LocalDate): Long = localDate.toEpochDay() * MILLIS_IN_DAY
}
