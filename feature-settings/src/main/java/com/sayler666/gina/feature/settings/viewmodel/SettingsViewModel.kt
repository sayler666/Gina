package com.sayler666.gina.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.data.database.db.journal.withRawDao
import com.sayler666.core.image.ImageOptimization.OptimizationSettings
import com.sayler666.gina.feature.settings.SettingsStorage
import com.sayler666.gina.feature.settings.reminder.NotActive
import com.sayler666.gina.feature.settings.reminder.RemindersViewModel
import com.sayler666.gina.feature.settings.reminder.ReminderState
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.Back
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.NavToManageFriends
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.ShowToast
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnDatabaseFileSelected
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnImageCompressionToggled
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnImageQualityChanged
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnIncognitoModeToggled
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnManageFriendsPressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnReminderCancel
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnReminderSet
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnThemeSelected
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnVacuumDatabasePressed
import com.sayler666.gina.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime
import javax.inject.Inject
import kotlin.time.measureTime

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val setting: SettingsStorage,
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val themeMapper: ThemeMapper,
    imageOptimizationViewModel: ImageOptimizationViewModel,
    remindersViewModel: RemindersViewModel
) : ViewModel() {

    private val imageOptimizationVM: ImageOptimizationViewModel = imageOptimizationViewModel
    private val remindersVM: RemindersViewModel = remindersViewModel

    private val mutableShowDbCardLoader = MutableStateFlow(false)

    private val mutableViewState = MutableStateFlow(createInitialState())
    val viewState: StateFlow<SettingsState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    init {
        with(imageOptimizationViewModel) { initialize() }
        with(remindersViewModel) { initialize() }

        observeDatabasePath()
        observeThemes()
        observeIncognitoMode()
        observeShowDbCardLoader()
        observeImageOptimizationSettings()
        observeReminderState()
    }

    private fun createInitialState() = SettingsState(
        databasePath = null,
        themes = emptyList(),
        incognitoMode = false,
        showDbCardLoader = false,
        imageOptimizationSettings = null,
        reminderState = NotActive
    )

    private fun observeDatabasePath() {
        setting.getDatabasePathFlow()
            .onEach { db ->
                mutableViewState.update { it.copy(databasePath = db) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeThemes() {
        setting.getThemeFlow().map { themeMapper.mapToVM(it) }
            .onEach { themes ->
                mutableViewState.update { it.copy(themes = themes) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeIncognitoMode() {
        setting.getIncognitoModeFlow()
            .onEach { incognito ->
                mutableViewState.update { it.copy(incognitoMode = incognito) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeShowDbCardLoader() {
        mutableShowDbCardLoader
            .onEach { loader ->
                mutableViewState.update { it.copy(showDbCardLoader = loader) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeImageOptimizationSettings() {
        imageOptimizationVM.imageOptimizationSettings
            .onEach { imgOpts ->
                mutableViewState.update { it.copy(imageOptimizationSettings = imgOpts) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeReminderState() {
        remindersVM.reminder
            .onEach { reminder ->
                mutableViewState.update { it.copy(reminderState = reminder) }
            }
            .launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnBackPressed -> mutableViewActions.trySend(Back)
            OnManageFriendsPressed -> mutableViewActions.trySend(NavToManageFriends)
            OnVacuumDatabasePressed -> vacuumDatabase()
            is OnThemeSelected -> viewModelScope.launch { setting.saveTheme(event.theme) }
            is OnIncognitoModeToggled -> viewModelScope.launch { setting.saveIncognitoMode(event.enabled) }
            is OnDatabaseFileSelected -> viewModelScope.launch { ginaDatabaseProvider.openAndRememberDB(event.path) }
            is OnImageQualityChanged -> imageOptimizationVM.setNewImageQuality(event.quality)
            is OnImageCompressionToggled -> imageOptimizationVM.toggleImageCompression(event.enabled)
            is OnReminderSet -> remindersVM.setReminder(event.time)
            OnReminderCancel -> remindersVM.removeReminders()
        }
    }

    private fun vacuumDatabase() {
        viewModelScope.launch {
            Timber.d("Vacuum started")
            mutableShowDbCardLoader.value = true
            val time = measureTime {
                try {
                    ginaDatabaseProvider.withRawDao { vacuum() }
                    mutableViewActions.trySend(ShowToast("Database vacuumed!"))
                } catch (e: Throwable) {
                    mutableViewActions.trySend(ShowToast("Error while vacuuming!"))
                    Timber.e(e)
                } finally {
                    mutableShowDbCardLoader.value = false
                }
            }
            Timber.d("Vacuum ended in: $time")
        }
    }

    sealed interface ViewEvent {
        data object OnBackPressed : ViewEvent
        data object OnManageFriendsPressed : ViewEvent
        data object OnVacuumDatabasePressed : ViewEvent
        data class OnThemeSelected(val theme: Theme) : ViewEvent
        data class OnIncognitoModeToggled(val enabled: Boolean) : ViewEvent
        data class OnDatabaseFileSelected(val path: String) : ViewEvent
        data class OnImageQualityChanged(val quality: Int) : ViewEvent
        data class OnImageCompressionToggled(val enabled: Boolean) : ViewEvent
        data class OnReminderSet(val time: LocalTime) : ViewEvent
        data object OnReminderCancel : ViewEvent
    }

    sealed interface ViewAction {
        data object Back : ViewAction
        data object NavToManageFriends : ViewAction
        data class ShowToast(val message: String) : ViewAction
    }
}
