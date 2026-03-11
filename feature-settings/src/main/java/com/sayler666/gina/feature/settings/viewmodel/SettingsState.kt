package com.sayler666.gina.feature.settings.viewmodel

import com.sayler666.core.image.ImageOptimization.OptimizationSettings
import com.sayler666.gina.reminders.viewmodel.ReminderState

data class SettingsState(
    val databasePath: String?,
    val themes: List<ThemeItem>,
    val incognitoMode: Boolean,
    val showDbCardLoader: Boolean,
    val imageOptimizationSettings: OptimizationSettings?,
    val reminderState: ReminderState,
)
