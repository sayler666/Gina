package com.sayler666.gina.db.converter

import androidx.room.TypeConverter
import mood.Mood
import mood.Mood.Companion.mapToMood

class MoodConverter {

    @TypeConverter
    fun toMood(moodIntValue: Int): Mood = moodIntValue.mapToMood()

    @TypeConverter
    fun fromMoodToInt(mood: Mood): Int = mood.numberValue

}
