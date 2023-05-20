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

@Dao
interface DaysDao {
    @Query("SELECT * FROM days ORDER by date DESC")
    fun getDaysFlow(): Flow<List<Day>>

    @Query("SELECT * FROM days WHERE content LIKE '%' || :searchQuery || '%' AND mood IN (:moods) ORDER by date DESC")
    fun getDaysWithFiltersFlow(searchQuery: String?, vararg moods: Int): Flow<List<Day>>

    @Transaction
    @Query("SELECT * FROM days WHERE id = :id")
    fun getDayFlow(id: Int): Flow<DayDetails>

    @Transaction
    @Query("SELECT id FROM days WHERE date > :date ORDER BY date ASC LIMIT 1")
    suspend fun getNextDayIdAfter(date: Long): Int?

    @Transaction
    @Query("SELECT id FROM days WHERE date < :date ORDER BY date DESC LIMIT 1")
    suspend fun getPreviousDayIdBefore(date: Long): Int?

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
