package com.sayler666.gina.reminder.usecase

import com.sayler666.gina.reminder.db.RemindersDatabaseProvider
import com.sayler666.gina.reminder.db.withRemindersDao
import com.sayler666.gina.reminder.viewmodel.ReminderEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastReminderUseCase @Inject constructor(
    private val remindersDatabaseProvider: RemindersDatabaseProvider,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(): Flow<ReminderEntity> = flow {
        remindersDatabaseProvider.withRemindersDao {
            emitAll(getLastReminderFlow().map { it.toReminderEntity() })
        }
    }.flowOn(coroutineDispatcher)
}
