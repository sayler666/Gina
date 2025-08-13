package com.sayler666.data.database.db.journal.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sayler666.data.database.db.journal.entity.AttachmentEntity
import com.sayler666.data.database.db.journal.entity.AttachmentWithDayEntity
import com.sayler666.data.database.db.journal.entity.DayDetailsEntity
import com.sayler666.data.database.db.journal.entity.DayEntity
import com.sayler666.data.database.db.journal.entity.DayFriendsEntity
import com.sayler666.data.database.db.journal.entity.FriendEntity
import com.sayler666.domain.model.journal.FriendWithCount
import com.sayler666.domain.model.journal.Mood
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DaysDao {
    @Query("SELECT * FROM days ORDER by date DESC")
    fun getDaysFlow(): Flow<List<DayEntity>>

    @Query(
        "SELECT * FROM days WHERE content LIKE '%' || :searchQuery || '%' " +
                "AND mood IN (:moods) ORDER by date DESC"
    )
    fun getDaysWithFiltersFlow(searchQuery: String?, vararg moods: Mood): Flow<List<DayEntity>>

    @Transaction
    @Query("SELECT * FROM days WHERE id = :id")
    fun getDayFlow(id: Int): Flow<DayDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM days WHERE id = :id")
    suspend fun getDay(id: Int): DayDetailsEntity?

    @Transaction
    @Query("SELECT * FROM days ORDER by date DESC LIMIT 1")
    suspend fun getLastDay(): DayDetailsEntity

    @Query("SELECT * FROM attachments WHERE attachment_id = :id")
    suspend fun getImage(id: Int): AttachmentEntity

    @Transaction
    @Query(
        "SELECT * FROM attachments " +
                "JOIN days ON attachments.days_id = days.id " +
                "WHERE attachments.attachment_id = :imageId"
    )
    suspend fun getAttachmentDay(imageId: Int): AttachmentWithDayEntity

    @Transaction
    @Query(
        "SELECT * FROM attachments JOIN days ON attachments.days_id = days.id" +
                " WHERE strftime('%m-%d', datetime(date/1000, 'unixepoch', 'localtime'))" +
                " = :currentDate"
    )
    fun getPreviousYearsAttachments(currentDate: String): Flow<List<AttachmentWithDayEntity>>

    @Query(
        "SELECT attachments.attachment_id FROM attachments " +
                "JOIN days ON attachments.days_id = days.id WHERE mime_type LIKE 'image/%' " +
                "ORDER BY days.date DESC, attachments.attachment_id DESC LIMIT :offset, 100"
    )
    suspend fun getImageAttachmentsIds(offset: Int = 0): List<Int>

    @Query("SELECT id FROM days WHERE date > :date and id != :id ORDER BY date ASC LIMIT 1")
    suspend fun getNextDayIdAfter(date: LocalDate, id: Int): Int?

    @Query("SELECT id FROM days WHERE date < :date and id != :id ORDER BY date DESC LIMIT 1")
    suspend fun getPreviousDayIdBefore(date: LocalDate, id: Int): Int?

    @Update
    suspend fun updateDay(day: DayEntity): Int

    @Insert
    suspend fun addDay(day: DayEntity): Long

    @Delete
    suspend fun deleteDay(day: DayEntity): Int

    @Insert
    suspend fun insertAttachments(attachments: List<AttachmentEntity>)

    @Delete
    suspend fun removeAttachments(attachments: List<AttachmentEntity>)

    @Query("SELECT * FROM friends WHERE friend_id = :id")
    fun getFriendFlow(id: Int): Flow<FriendEntity>

    @Query(
        "SELECT friends.friend_id as friendId, friends.name as friendName," +
                "friends.avatar as friendAvatar, " +
                "COUNT(daysFriends.friend_id) as daysCount FROM friends " +
                "LEFT JOIN daysFriends ON friends.friend_id = daysFriends.friend_id " +
                "GROUP BY friends.friend_id ORDER BY friendId DESC"
    )
    fun getFriendsWithCountFlow(): Flow<List<FriendWithCount>>


    @Query(
        """
        SELECT 
            friends.friend_id AS friendId, 
            friends.name AS friendName,
            friends.avatar AS friendAvatar, 
            COUNT(daysFriends.friend_id) AS daysCount, 
            (SELECT COUNT(*) 
             FROM daysFriends df
             JOIN days d ON df.id = d.id
             WHERE df.friend_id = friends.friend_id 
               AND d.date >= (strftime('%s', 'now', '-1 month') * 1000)
               AND d.date <= (strftime('%s', 'now') * 1000)
            ) AS recentMonthCount
        FROM friends
        LEFT JOIN daysFriends ON friends.friend_id = daysFriends.friend_id 
        GROUP BY friends.friend_id, friends.name, friends.avatar
        ORDER BY recentMonthCount DESC, daysCount DESC;
    """
    )
    fun getFriendsWithCountByRecentFlow(): Flow<List<FriendWithCount>>

    @Query(
        "SELECT friends.friend_id as friendId, friends.name as friendName," +
                "friends.avatar as friendAvatar, " +
                "COUNT(daysFriends.friend_id) as daysCount FROM friends " +
                "LEFT JOIN daysFriends ON friends.friend_id = daysFriends.friend_id " +
                "LEFT JOIN days ON days.id = daysFriends.id " +
                "WHERE content LIKE '%' || :searchQuery || '%' " +
                "AND mood IN (:moods)" +
                "AND days.date >= :dateFrom and days.date <= :dateTo " +
                "GROUP BY friends.friend_id ORDER BY friendId DESC"
    )
    suspend fun getFriendsWithCount(
        searchQuery: String?,
        dateFrom: LocalDate,
        dateTo: LocalDate,
        vararg moods: Mood,
    ): List<FriendWithCount>

    @Query("DELETE FROM daysFriends WHERE id = :id")
    suspend fun deleteFriendsForDay(id: Int): Int

    @Insert
    suspend fun addFriendsToDay(daysFriends: List<DayFriendsEntity>)

    @Insert
    suspend fun addFriend(friend: FriendEntity): Long

    @Update
    suspend fun updateFriend(friend: FriendEntity): Int

    @Delete
    suspend fun deleteFriend(friend: FriendEntity): Int
}
