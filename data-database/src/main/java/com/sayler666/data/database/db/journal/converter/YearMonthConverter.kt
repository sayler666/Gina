package com.sayler666.data.database.db.journal.converter

import androidx.room.TypeConverter
import java.time.YearMonth

class YearMonthConverter {

    @TypeConverter
    fun toYearMonth(yearMonth: String): YearMonth = YearMonth.parse(yearMonth)

}
