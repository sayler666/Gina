package com.sayler.data.days.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "days")
data class Day(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
        @ColumnInfo(name = "date") val date: Long,
        @ColumnInfo(name = "content") val content: String
) : GinaEntity
