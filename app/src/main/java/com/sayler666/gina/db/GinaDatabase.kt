package com.sayler666.gina.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Update
import com.sayler666.gina.db.converter.DateConverter
import com.sayler666.gina.db.converter.MoodConverter
import kotlinx.coroutines.flow.Flow
import com.sayler666.gina.mood.Mood
import java.time.LocalDate


@Database(
    entities = [Day::class, Attachment::class, Friend::class, DayFriends::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MoodConverter::class, DateConverter::class)
abstract class GinaDatabase : RoomDatabase() {
    abstract fun daysDao(): DaysDao
}

@Entity(tableName = "days")
data class Day(
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: LocalDate?,

    @ColumnInfo(name = "content", typeAffinity = ColumnInfo.TEXT)
    val content: String?,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "mood", typeAffinity = ColumnInfo.INTEGER)
    val mood: Mood?,
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

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "friend_id")
    val id: Int = 0,

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
    val id: Int,
    @ColumnInfo(name = "friend_id")
    val friendId: Int
)

data class FriendWithCount(
    val friendId: Int,
    val friendName: String,
    val friendAvatar: ByteArray?,
    val daysCount: Int
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

data class AttachmentWithDay(
    @Embedded val attachment: Attachment,
    @Embedded val day: Day
)

@Dao
interface DaysDao {
    @Query("SELECT * FROM days ORDER by date DESC")
    fun getDaysFlow(): Flow<List<Day>>

    @Query(
        "SELECT * FROM days WHERE content LIKE '%' || :searchQuery || '%' " +
                "AND mood IN (:moods) ORDER by date DESC"
    )
    fun getDaysWithFiltersFlow(searchQuery: String?, vararg moods: Mood): Flow<List<Day>>

    @Transaction
    @Query("SELECT * FROM days WHERE id = :id")
    fun getDayFlow(id: Int): Flow<DayDetails>

    @Transaction
    @Query("SELECT * FROM days ORDER by date DESC LIMIT 1")
    suspend fun getLastDay(): DayDetails

    @Query("SELECT * FROM attachments WHERE attachment_id = :id")
    suspend fun getImage(id: Int): Attachment

    @Transaction
    @Query(
        "SELECT * FROM attachments " +
                "JOIN days ON attachments.days_id = days.id " +
                "WHERE attachments.attachment_id = :imageId"
    )
    suspend fun getAttachmentDay(imageId: Int): AttachmentWithDay

    @Query(
        "SELECT attachments.attachment_id FROM attachments " +
                "JOIN days ON attachments.days_id = days.id WHERE mime_type LIKE 'image/%' " +
                "ORDER BY days.date DESC, attachments.attachment_id DESC LIMIT :offset, 100"
    )
    suspend fun getImageAttachmentsIds(offset: Int = 0): List<Int>

    @Transaction
    @Query("SELECT id FROM days WHERE date > :date ORDER BY date ASC LIMIT 1")
    suspend fun getNextDayIdAfter(date: LocalDate): Int?

    @Transaction
    @Query("SELECT id FROM days WHERE date < :date ORDER BY date DESC LIMIT 1")
    suspend fun getPreviousDayIdBefore(date: LocalDate): Int?

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

    @Query("SELECT * FROM friends WHERE friend_id = :id")
    fun getFriendFlow(id: Int): Flow<Friend>

    @Query(
        "SELECT friends.friend_id as friendId, friends.name as friendName," +
                "friends.avatar as friendAvatar, " +
                "COUNT(daysFriends.friend_id) as daysCount FROM friends " +
                "LEFT JOIN daysFriends ON friends.friend_id = daysFriends.friend_id " +
                "GROUP BY friends.friend_id ORDER BY friendId DESC"
    )
    fun getFriendsWithCountFlow(): Flow<List<FriendWithCount>>

    @Query("DELETE FROM daysFriends WHERE id = :id")
    suspend fun deleteFriendsForDay(id: Int): Int

    @Insert
    suspend fun addFriendsToDay(daysFriends: List<DayFriends>)

    @Insert
    suspend fun addFriend(friend: Friend): Long

    @Update
    suspend fun updateFriend(friend: Friend): Int

    @Delete
    suspend fun deleteFriend(friend: Friend): Int
}
