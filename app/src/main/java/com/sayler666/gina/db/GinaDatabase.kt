package com.sayler666.gina.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Database(
    entities = [Day::class, Attachment::class, Friend::class, DayFriends::class],
    version = 1,
    exportSchema = false
)
abstract class GinaDatabase : RoomDatabase() {
    abstract fun daysDao(): DaysDao
}

@Entity(tableName = "days")
data class Day(
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: Long?,

    @ColumnInfo(name = "content", typeAffinity = ColumnInfo.TEXT)
    val content: String?,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "mood", typeAffinity = ColumnInfo.INTEGER)
    val mood: Int?,
)

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
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
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
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (dayId ?: 0)
        result = 31 * result + (content?.contentHashCode() ?: 0)
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        return result
    }
}

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "friend_id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "avatar", typeAffinity = ColumnInfo.BLOB)
    val avatar: ByteArray?
)

@Entity(
    tableName = "daysFriends",
    primaryKeys = ["id", "friend_id"],
    foreignKeys = [
        ForeignKey(
            entity = Day::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Friend::class,
            parentColumns = ["friend_id"],
            childColumns = ["friend_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DayFriends(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "friend_id")
    val friendId: Long
)

data class DayDetails(
    @Embedded val day: Day,
    @Relation(
        parentColumn = "id",
        entityColumn = "days_id"
    )
    val attachments: List<Attachment>,
    @Relation(
        parentColumn = "id",
        entityColumn = "friend_id",
        associateBy = Junction(DayFriends::class)
    )
    val friends: List<Friend>
)

@Dao
interface DaysDao {
    @Query("SELECT * FROM days ORDER by date DESC")
    fun getDaysFlow(): Flow<List<Day>>

    @Query("SELECT * FROM days WHERE content LIKE '%' || :searchQuery || '%' ORDER by date DESC")
    fun getDaysFlow(searchQuery: String?): Flow<List<Day>>

    @Transaction
    @Query("SELECT * FROM days WHERE id = :id")
    fun getDayFlow(id: Int): Flow<DayDetails>

    @Query("SELECT * FROM friends")
    fun getFriendFlow(): Flow<List<Friend>>

    @Update
    suspend fun updateDay(day: Day): Int

    @Insert
    suspend fun addDay(day: Day): Long

    @Delete
    suspend fun deleteDay(day: Day): Int

    @Insert
    suspend fun insertAttachments(attachments: List<Attachment>)

    @Delete
    suspend fun removeAttachments(attachments: List<Attachment>)
}
