package com.sayler666.gina.feature.settings.viewmodel

import com.sayler666.gina.reminders.viewmodel.ReminderState

data class SettingsState(
    val databaseExternalPath: String? = null,
    val databaseSize: Long? = null,
    val themes: List<ThemeItem>,
    val incognitoMode: Boolean,
    val showDbCardLoader: Boolean,
    val reminderState: ReminderState,
)
