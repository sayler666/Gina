package com.sayler666.data.database.db.journal.converter

import androidx.room.TypeConverter
import com.sayler666.domain.model.journal.Mood
import com.sayler666.domain.model.journal.Mood.Companion.mapToMood

class MoodConverter {

    @TypeConverter
    fun toMood(moodIntValue: Int): Mood = moodIntValue.mapToMood()

    @TypeConverter
    fun fromMoodToInt(mood: Mood): Int = mood.numberValue

}
