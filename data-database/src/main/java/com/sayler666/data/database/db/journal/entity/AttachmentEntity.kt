package com.sayler666.data.database.db.journal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.sayler666.domain.model.journal.Attachment

@Entity(
    tableName = "attachments", foreignKeys = [ForeignKey(
        entity = DayEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("days_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "attachment_id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "days_id", typeAffinity = ColumnInfo.INTEGER)
    val dayId: Int?,

    @ColumnInfo(name = "file", typeAffinity = ColumnInfo.BLOB)
    val content: ByteArray?,

    @ColumnInfo(name = "mime_type", typeAffinity = ColumnInfo.TEXT)
    val mimeType: String?,

    @ColumnInfo(name = "hidden", typeAffinity = ColumnInfo.INTEGER)
    val hidden: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttachmentEntity

        if (id != other.id) return false
        if (dayId != other.dayId) return false
        if (content != null) {
            if (other.content == null) return false
            if (!content.contentEquals(other.content)) return false
        } else if (other.content != null) return false
        if (mimeType != other.mimeType) return false
        return hidden == other.hidden
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (dayId ?: 0)
        result = 31 * result + (content?.contentHashCode() ?: 0)
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        result = 31 * result + hidden.hashCode()
        return result
    }

    companion object {
        fun AttachmentEntity.toModel() = Attachment(
            id = id,
            dayId = dayId,
            content = content!!,
            mimeType = mimeType!!,
            hidden = hidden,
        )

        fun Attachment.toEntity() = AttachmentEntity(
            id = id,
            dayId = dayId,
            content = content,
            mimeType = mimeType,
            hidden = hidden,
        )
    }
}
