package com.sayler666.data.database.db.journal

import android.database.SQLException
import androidx.room.withTransaction
import com.sayler666.data.database.db.journal.dao.DaysDao
import com.sayler666.data.database.db.journal.entity.AttachmentEntity.Companion.toEntity
import com.sayler666.data.database.db.journal.entity.AttachmentEntity.Companion.toModel
import com.sayler666.data.database.db.journal.entity.AttachmentIdWithDate
import com.sayler666.data.database.db.journal.entity.AttachmentWithDayEntity.Companion.toModel
import com.sayler666.data.database.db.journal.entity.DayDetailsEntity.Companion.toModel
import com.sayler666.data.database.db.journal.entity.DayEntity.Companion.toEntity
import com.sayler666.data.database.db.journal.entity.DayEntity.Companion.toModel
import com.sayler666.data.database.db.journal.entity.DayFriendsEntity
import com.sayler666.data.database.db.journal.entity.FriendEntity.Companion.toEntity
import com.sayler666.data.database.db.journal.entity.FriendEntity.Companion.toModel
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.AttachmentWithDay
import com.sayler666.domain.model.journal.Day
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.FriendWithCount
import com.sayler666.domain.model.journal.Mood
import com.sayler666.domain.model.journal.MoodAverage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class JournalRepository @Inject constructor(
    private val daysDao: DaysDao,
    private val db: GinaDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun daysFlow(): Flow<List<Day>> = flow {
        emitAll(daysDao.getDaysFlow().map { dayEntities -> dayEntities.map { it.toModel() } })
    }.flowOn(dispatcher)

    fun daysWithFiltersFlow(
        searchQuery: String?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        vararg moods: Mood,
    ): Flow<List<Day>> = flow {
        emitAll(daysDao.getDaysWithFiltersFlow(searchQuery, dateFrom, dateTo, *moods).map { dayEntities ->
            dayEntities.map { it.toModel() }
        })
    }.flowOn(dispatcher)

    fun previousYearsAttachments(date: LocalDate): Flow<List<AttachmentWithDay>> = flow {
        val dateString = date.format(DateTimeFormatter.ofPattern("MM-dd"))
        emitAll(daysDao.getPreviousYearsAttachments(dateString).map { attachments ->
            attachments.map { it.toModel() }
        })
    }.flowOn(dispatcher)

    suspend fun addDay(dayDetails: DayDetails) {
        db.withTransaction {
            val dayId = daysDao.addDay(dayDetails.day.toEntity()).toInt()
            addAttachments(dayDetails, dayId)
            addFriends(dayDetails, dayId)
        }
    }

    suspend fun updateDay(dayDetails: DayDetails, attachmentsToDelete: List<Attachment>) {
        try {
            db.withTransaction {
                daysDao.updateDay(dayDetails.day.toEntity())
                updateAttachments(dayDetails, attachmentsToDelete)
                updateFriends(dayDetails)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    private suspend fun updateAttachments(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    ) {
        val attachmentsToAdd = dayDetails.attachments.toMutableList()
            .filter { it.dayId == null }
            .map { it.copy(dayId = dayDetails.day.id) }
        if (attachmentsToAdd.isNotEmpty()) daysDao.insertAttachments(attachmentsToAdd.map { it.toEntity() })
        if (attachmentsToDelete.isNotEmpty()) daysDao.removeAttachments(attachmentsToDelete.map { it.toEntity() })
    }

    private suspend fun updateFriends(dayDetails: DayDetails) {
        dayDetails.day.id.let { dayId ->
            daysDao.deleteFriendsForDay(dayDetails.day.id)
            val dayFriends = dayDetails.friends.map { friend ->
                DayFriendsEntity(dayId, friend.id)
            }
            if (dayDetails.friends.isNotEmpty()) daysDao.addFriendsToDay(dayFriends)
        }
    }

    fun daysFlow(id: Int): Flow<DayDetails?> = flow {
        emitAll(daysDao.getDayFlow(id).map { dayDetails ->
            dayDetails?.toModel()
        })
    }.flowOn(dispatcher)

    suspend fun getNextDayId(currentDatId: Int): Result<Int> = try {
        val currentDay = daysDao.getDay(currentDatId)
        val nextDayId: Int? = if (currentDay?.day?.id != null && currentDay.day.date != null)
            daysDao.getNextDayIdAfter(currentDay.day.date, currentDay.day.id) else null
        nextDayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No next day found"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    suspend fun getPreviousDayId(currentDatId: Int): Result<Int> = try {
        val currentDay = daysDao.getDay(currentDatId)
        val previousDayId: Int? = if (currentDay?.day?.id != null && currentDay.day.date != null)
            daysDao.getPreviousDayIdBefore(currentDay.day.date, currentDay.day.id) else null
        previousDayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No previous day found"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    suspend fun getDay(id: Int): DayDetails? = daysDao.getDay(id)?.toModel()

    fun getAllFriendsWithCountFlow(): Flow<List<FriendWithCount>> = flow {
        try {
            emitAll(daysDao.getFriendsWithCountFlow())
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    fun getAllFriendsWithCountByRecentFlow(): Flow<List<FriendWithCount>> = flow {
        try {
            emitAll(daysDao.getFriendsWithCountByRecentFlow())
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    suspend fun getAllFriendsWithCount(
        searchQuery: String,
        moods: List<Mood>,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): List<FriendWithCount> = try {
        daysDao.getFriendsWithCount(searchQuery, dateFrom, dateTo, *moods.toTypedArray())
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }

    fun getAttachmentWithDayFlow(attachmentId: Int): Flow<AttachmentWithDay?> = flow {
        try {
            emit(daysDao.getAttachmentDay(attachmentId).toModel())
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    suspend fun getAttachmentWithDay(attachmentId: Int): AttachmentWithDay? = try {
        daysDao.getAttachmentDay(attachmentId).toModel()
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        null
    }

    suspend fun deleteDay(dayDetails: DayDetails) {
        try {
            daysDao.deleteDay(dayDetails.day.toEntity())
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    fun getFriendFlow(id: Int): Flow<Friend?> = flow {
        try {
            emitAll(daysDao.getFriendFlow(id).map { it.toModel() })
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    suspend fun editFriend(friend: Friend) {
        try {
            daysDao.updateFriend(friend.toEntity())
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    private suspend fun addFriends(
        dayDetails: DayDetails,
        dayId: Int
    ) {
        dayDetails.friends
            .map { friend -> DayFriendsEntity(dayId, friend.id) }
            .let { daysDao.addFriendsToDay(it) }
    }

    suspend fun deleteFriend(friend: Friend) {
        try {
            daysDao.deleteFriend(friend.toEntity())
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    suspend fun getImageAttachmentsIds(offset: Int): List<AttachmentIdWithDate> = try {
        daysDao.getImageAttachmentsIds(offset)
    } catch (e: SQLException) {
        Timber.e(e, "ImageRepository: Database error")
        emptyList()
    }

    suspend fun getAllImageAttachmentIds(): List<Int> = try {
        daysDao.getAllImageAttachmentIds()
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }

    suspend fun getImageAttachmentIdsForDay(dayId: Int): List<Int> = try {
        daysDao.getImageAttachmentIdsForDay(dayId)
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }

    suspend fun getImage(id: Int): Attachment? = try {
        daysDao.getImage(id).toModel()
    } catch (e: SQLException) {
        Timber.e(e, "ImageRepository: Database error")
        null
    }

    private suspend fun addAttachments(
        dayDetails: DayDetails,
        dayId: Int
    ) {
        dayDetails.attachments.toMutableList()
            .map { it.copy(dayId = dayId).toEntity() }
            .let { daysDao.insertAttachments(it) }
    }

    suspend fun getAvgMoodsByMonth(): List<MoodAverage> = try {
        daysDao.getAvgMoodsByMonth()
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }

    suspend fun getAvgMoodsByWeek(): List<MoodAverage> = try {
        daysDao.getAvgMoodsByWeek()
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }

    suspend fun updateAttachmentHidden(id: Int, hidden: Boolean) {
        try {
            daysDao.updateAttachmentHidden(id, hidden)
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

}
