package com.sayler666.data.database.db.quotes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "quote", typeAffinity = ColumnInfo.TEXT)
    val quote: String,

    @ColumnInfo(name = "author", typeAffinity = ColumnInfo.TEXT)
    val author: String,

    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: LocalDate,
)
