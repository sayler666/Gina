package com.sayler.data.days.entity

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.BLOB
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "attachments", foreignKeys = arrayOf(
        ForeignKey(entity = Day::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("days_id"),
                onDelete = ForeignKey.CASCADE)
))
data class Attachment(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
        @ColumnInfo(name = "days_id") val dayId: Long,
        @ColumnInfo(name = "file", typeAffinity = BLOB) val file: ByteArray,
        @ColumnInfo(name = "mime_type") val mimeType: String
) : GinaEntity
