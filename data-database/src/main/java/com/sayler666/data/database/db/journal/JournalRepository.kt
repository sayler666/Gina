package com.sayler666.data.database.db.journal

import android.database.SQLException
import com.sayler666.data.database.db.journal.entity.AttachmentEntity.Companion.toEntity
import com.sayler666.data.database.db.journal.entity.AttachmentEntity.Companion.toModel
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
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun daysFlow(): Flow<List<Day>> = flow {
        val daysFlow = ginaDatabaseProvider.returnWithDaysDao { getDaysFlow() }
        daysFlow?.let { flow ->
            emitAll(flow.map { dayEntities -> dayEntities.map { it.toModel() } })
        }
    }.flowOn(dispatcher)

    fun daysWithFiltersFlow(searchQuery: String?, vararg moods: Mood): Flow<List<Day>> = flow {
        ginaDatabaseProvider.withDaysDao {
            emitAll(getDaysWithFiltersFlow(searchQuery, *moods).map { dayEntities ->
                dayEntities.map { it.toModel() }
            })
        }
    }.flowOn(dispatcher)

    fun previousYearsAttachments(date: LocalDate): Flow<List<AttachmentWithDay>> = flow {
        val dateString = date.format(DateTimeFormatter.ofPattern("MM-dd"))
        ginaDatabaseProvider.withDaysDao {
            emitAll(getPreviousYearsAttachments(dateString).map { attachments ->
                attachments.map { it.toModel() }
            })
        }
    }.flowOn(dispatcher)

    suspend fun addDay(dayDetails: DayDetails) {
        ginaDatabaseProvider.transactionWithDaysDao {
            val dayId = addDay(dayDetails.day.toEntity()).toInt()
            addAttachments(dayDetails, dayId)
            addFriends(dayDetails, dayId)
        }
    }

    suspend fun updateDay(dayDetails: DayDetails, attachmentsToDelete: List<Attachment>) {
        try {
            ginaDatabaseProvider.transactionWithDaysDao {
                updateDay(dayDetails.day.toEntity())
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
        ginaDatabaseProvider.withDaysDao {
            if (attachmentsToAdd.isNotEmpty()) insertAttachments(attachmentsToAdd.map { it.toEntity() })
            if (attachmentsToDelete.isNotEmpty()) removeAttachments(attachmentsToDelete.map { it.toEntity() })
        }
    }

    private suspend fun updateFriends(dayDetails: DayDetails) {
        dayDetails.day.id.let { dayId ->
            ginaDatabaseProvider.withDaysDao {
                deleteFriendsForDay(dayDetails.day.id)
                val dayFriends = dayDetails.friends.map { friend ->
                    DayFriendsEntity(dayId, friend.id)
                }
                if (dayDetails.friends.isNotEmpty()) addFriendsToDay(dayFriends)
            }
        }
    }

    fun daysFlow(id: Int): Flow<DayDetails?> = flow {
        ginaDatabaseProvider.withDaysDao {
            emitAll(getDayFlow(id).map { dayDetails ->
                dayDetails?.toModel()
            })
        }
    }.flowOn(dispatcher)

    suspend fun getNextDayId(currentDatId: Int): Result<Int> = try {
        val currentDay = ginaDatabaseProvider.returnWithDaysDao {
            getDay(currentDatId)
        }
        val nextDayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            if (currentDay?.day?.id != null && currentDay.day.date != null)
                getNextDayIdAfter(currentDay.day.date, currentDay.day.id) else null
        }
        nextDayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No next day found"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    suspend fun getPreviousDayId(currentDatId: Int): Result<Int> = try {
        val currentDay = ginaDatabaseProvider.returnWithDaysDao {
            getDay(currentDatId)
        }
        val previousDayId: Int? = ginaDatabaseProvider.returnWithDaysDao {
            if (currentDay?.day?.id != null && currentDay.day.date != null)
                getPreviousDayIdBefore(currentDay.day.date, currentDay.day.id) else null
        }
        previousDayId?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No previous day found"))

    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    suspend fun getDay(id: Int): DayDetails? = ginaDatabaseProvider.returnWithDaysDao {
        getDay(id)?.toModel()
    }

    fun getAllFriendsWithCountFlow(): Flow<List<FriendWithCount>> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emitAll(getFriendsWithCountFlow())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    fun getAllFriendsWithCountByRecentFlow(): Flow<List<FriendWithCount>> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emitAll(getFriendsWithCountByRecentFlow())
            }
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
        (ginaDatabaseProvider.returnWithDaysDao {
            getFriendsWithCount(searchQuery, dateFrom, dateTo, *moods.toTypedArray())
        } ?: emptyList())
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }

    fun getAttachmentWithDayFlow(attachmentId: Int): Flow<AttachmentWithDay?> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emit(getAttachmentDay(attachmentId).toModel())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    suspend fun deleteDay(dayDetails: DayDetails) {
        try {
            ginaDatabaseProvider.withDaysDao {
                deleteDay(dayDetails.day.toEntity())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    fun getFriendFlow(id: Int): Flow<Friend?> = flow {
        try {
            ginaDatabaseProvider.withDaysDao {
                emitAll(getFriendFlow(id).map { it.toModel() })
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }.flowOn(dispatcher)

    suspend fun editFriend(friend: Friend) {
        try {
            ginaDatabaseProvider.withDaysDao {
                updateFriend(friend.toEntity())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    private suspend fun addFriends(
        dayDetails: DayDetails,
        dayId: Int
    ) {
        ginaDatabaseProvider.withDaysDao {
            dayDetails.friends
                .map { friend -> DayFriendsEntity(dayId, friend.id) }
                .let { addFriendsToDay(it) }
        }
    }

    suspend fun deleteFriend(friend: Friend) {
        try {
            ginaDatabaseProvider.withDaysDao {
                deleteFriend(friend.toEntity())
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    suspend fun getImageAttachmentsIds(offset: Int): List<Int> = try {
        ginaDatabaseProvider.returnWithDaysDao {
            getImageAttachmentsIds(offset)
        } ?: emptyList()
    } catch (e: SQLException) {
        Timber.e(e, "ImageRepository: Database error")
        emptyList()
    }

    suspend fun getImage(id: Int): Attachment? = try {
        ginaDatabaseProvider.returnWithDaysDao {
            getImage(id).toModel()
        }
    } catch (e: SQLException) {
        Timber.e(e, "ImageRepository: Database error")
        null
    }

    private suspend fun addAttachments(
        dayDetails: DayDetails,
        dayId: Int
    ) {
        ginaDatabaseProvider.withDaysDao {
            dayDetails.attachments.toMutableList()
                .map { it.copy(dayId = dayId).toEntity() }
                .let { insertAttachments(it) }
        }
    }

    suspend fun getAvgMoodsByMonth(): List<MoodAverage> = try {
        (ginaDatabaseProvider.returnWithDaysDao {
            getAvgMoodsByMonth()
        } ?: emptyList())
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }

    suspend fun getAvgMoodsByWeek(): List<MoodAverage> = try {
        (ginaDatabaseProvider.returnWithDaysDao {
            getAvgMoodsByWeek()
        } ?: emptyList())
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        emptyList()
    }

}
