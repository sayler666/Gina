package com.sayler666.data.database.db.reminders

import com.sayler666.data.database.db.reminders.ReminderEntity.Companion.toModel
import com.sayler666.domain.model.reminders.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RemindersRepository @Inject constructor(

    private val remindersDatabaseProvider: RemindersDatabaseProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun getLastReminderFlow(): Flow<Reminder?> = flow {
        remindersDatabaseProvider.withRemindersDao {
            emitAll(getLastReminderFlow().map { it?.toModel() })
        }
    }.flowOn(dispatcher)
}
