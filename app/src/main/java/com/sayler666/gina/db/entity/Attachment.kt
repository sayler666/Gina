package com.sayler666.gina.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "attachments", foreignKeys = [ForeignKey(
        entity = Day::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("days_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Attachment(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "attachment_id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "days_id", typeAffinity = ColumnInfo.INTEGER)
    val dayId: Int?,

    @ColumnInfo(name = "file", typeAffinity = ColumnInfo.BLOB)
    val content: ByteArray?,

    @ColumnInfo(name = "mime_type", typeAffinity = ColumnInfo.TEXT)
    val mimeType: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (id != other.id) return false
        if (dayId != other.dayId) return false
        if (content != null) {
            if (other.content == null) return false
            if (!content.contentEquals(other.content)) return false
        } else if (other.content != null) return false
        return mimeType == other.mimeType
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (dayId ?: 0)
        result = 31 * result + (content?.contentHashCode() ?: 0)
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        return result
    }
}
