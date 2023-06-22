package com.sayler666.gina.db.converter

import androidx.room.TypeConverter
import mood.Mood
import mood.Mood.Companion.mapToMood

class MoodConverter {

    @TypeConverter
    fun toZonedDateTime(moodIntValue: Int): Mood = moodIntValue.mapToMood()

    @TypeConverter
    fun fromZonedDateTime(mood: Mood): Int = mood.numberValue

}
