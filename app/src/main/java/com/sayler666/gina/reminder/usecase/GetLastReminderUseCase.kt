package com.sayler666.gina.reminder.usecase

import com.sayler666.data.database.db.reminders.RemindersRepository
import com.sayler666.domain.model.reminders.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastReminderUseCase @Inject constructor(
    private val remindersRepository: RemindersRepository
) {

    operator fun invoke(): Flow<Reminder?> = remindersRepository.getLastReminderFlow()
}
