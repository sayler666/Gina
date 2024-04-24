package com.sayler666.gina.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sayler666.gina.db.entity.Attachment
import com.sayler666.gina.db.entity.AttachmentWithDay
import com.sayler666.gina.db.entity.Day
import com.sayler666.gina.db.entity.DayDetails
import com.sayler666.gina.db.entity.DayFriends
import com.sayler666.gina.db.entity.Friend
import com.sayler666.gina.db.entity.FriendWithCount
import com.sayler666.gina.mood.Mood
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
    @Query("SELECT * FROM days WHERE id = :id")
    suspend fun getDay(id: Int): DayDetails

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

    @Transaction
    @Query(
        "SELECT * FROM attachments JOIN days ON attachments.days_id = days.id" +
                " WHERE strftime('%m-%d', datetime(date/1000, 'unixepoch', 'localtime'))" +
                " = strftime('%m-%d',date())"
    )
    fun getPreviousYearsAttachments(): Flow<List<AttachmentWithDay>>

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

    @Query(
        "SELECT friends.friend_id as friendId, friends.name as friendName," +
                "friends.avatar as friendAvatar, " +
                "COUNT(daysFriends.friend_id) as daysCount FROM friends " +
                "LEFT JOIN daysFriends ON friends.friend_id = daysFriends.friend_id " +
                "LEFT JOIN days ON days.id = daysFriends.id " +
                "WHERE content LIKE '%' || :searchQuery || '%' " +
                "AND mood IN (:moods)" +
                "GROUP BY friends.friend_id ORDER BY friendId DESC"
    )
    suspend fun getFriendsWithCount(searchQuery: String?, vararg moods: Mood): List<FriendWithCount>

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
