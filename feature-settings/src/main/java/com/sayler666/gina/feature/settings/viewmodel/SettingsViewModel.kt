package com.sayler666.gina.feature.settings.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.navigation.BottomNavigationVisibilityManager
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.data.database.db.journal.withRawDao
import com.sayler666.data.database.db.reminders.ReminderEntity
import com.sayler666.gina.feature.settings.SettingsStorage
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.Back
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.NavToManageFriends
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.RestartApp
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.ShowToast
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnDatabaseFileSelected
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnExportDatabaseRequested
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnIncognitoModeToggled
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnManageFriendsPressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnNewDatabaseCreated
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnReminderCancel
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnReminderSet
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnThemeSelected
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnVacuumDatabasePressed
import com.sayler666.gina.reminders.usecase.AddReminderUseCase
import com.sayler666.gina.reminders.usecase.GetLastReminderUseCase
import com.sayler666.gina.reminders.usecase.RemoveAllRemindersUseCase
import com.sayler666.gina.reminders.usecase.toReminderState
import com.sayler666.gina.reminders.viewmodel.NotActive
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
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
    private val getLastReminderUseCase: GetLastReminderUseCase,
    private val addReminderUseCase: AddReminderUseCase,
    private val removeAllRemindersUseCase: RemoveAllRemindersUseCase,
) : ViewModel() {

    private val mutableShowDbCardLoader = MutableStateFlow(false)

    private val mutableViewState = MutableStateFlow(createInitialState())
    val viewState: StateFlow<SettingsState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    init {
        observeDatabaseExternalPath()
        observeDatabaseSize()
        observeThemes()
        observeIncognitoMode()
        observeShowDbCardLoader()
        observeReminderState()
    }

    private fun createInitialState() = SettingsState(
        databaseSize = dbSize(),
        themes = emptyList(),
        incognitoMode = false,
        showDbCardLoader = false,
        reminderState = NotActive
    )

    private fun observeDatabaseSize() {
        ginaDatabaseProvider.dbInvalidations
            .onEach { mutableViewState.update { it.copy(databaseSize = dbSize()) } }
            .launchIn(viewModelScope)
    }

    private fun dbSize() = ginaDatabaseProvider.getLocalDbFile()?.length()?.takeIf { it > 0L }

    private fun observeDatabaseExternalPath() {
        ginaDatabaseProvider.getDatabaseExternalPathFlow()
            .onEach { path ->
                mutableViewState.update { it.copy(databaseExternalPath = path) }
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

    private fun observeReminderState() {
        getLastReminderUseCase()
            .map { it.toReminderState() }
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
            OnHideBottomBar -> bottomNavigationVisibilityManager.hide()
            OnShowBottomBar -> bottomNavigationVisibilityManager.show()
            is OnThemeSelected -> viewModelScope.launch { setting.saveTheme(event.theme) }
            is OnIncognitoModeToggled -> viewModelScope.launch { setting.saveIncognitoMode(event.enabled) }
            is OnDatabaseFileSelected -> viewModelScope.launch {
                if (ginaDatabaseProvider.importFromUri(event.uri))
                    mutableViewActions.trySend(RestartApp)
            }
            is OnNewDatabaseCreated -> viewModelScope.launch {
                if (ginaDatabaseProvider.createNewDb(event.uri))
                    mutableViewActions.trySend(RestartApp)
            }
            is OnExportDatabaseRequested -> viewModelScope.launch {
                val success = ginaDatabaseProvider.exportToUri(event.uri)
                mutableViewActions.trySend(
                    ShowToast(if (success) "Database exported!" else "Error while exporting!")
                )
            }
            is OnReminderSet -> viewModelScope.launch {
                removeAllRemindersUseCase()
                addReminderUseCase(ReminderEntity(time = event.time))
            }
            OnReminderCancel -> viewModelScope.launch {
                removeAllRemindersUseCase()
            }
        }
    }

    private fun vacuumDatabase() {
        viewModelScope.launch {
            Timber.d("Vacuum started")
            mutableShowDbCardLoader.value = true
            val time = measureTime {
                try {
                    ginaDatabaseProvider.withRawDao { vacuum() }
                    ginaDatabaseProvider.sync()
                    mutableViewActions.trySend(ShowToast("Database vacuumed!"))
                } catch (e: Throwable) {
                    mutableViewActions.trySend(ShowToast("Error while vacuuming!"))
                    Timber.e(e)
                } finally {
                    mutableShowDbCardLoader.value = false
                    mutableViewState.update { it.copy(databaseSize = dbSize()) }
                }
            }
            Timber.d("Vacuum ended in: $time")
        }
    }

    sealed interface ViewEvent {
        data object OnBackPressed : ViewEvent
        data object OnManageFriendsPressed : ViewEvent
        data object OnVacuumDatabasePressed : ViewEvent
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
        data class OnThemeSelected(val theme: Theme) : ViewEvent
        data class OnIncognitoModeToggled(val enabled: Boolean) : ViewEvent
        data class OnDatabaseFileSelected(val uri: Uri) : ViewEvent
        data class OnNewDatabaseCreated(val uri: Uri) : ViewEvent
        data class OnExportDatabaseRequested(val uri: Uri) : ViewEvent
        data class OnReminderSet(val time: LocalTime) : ViewEvent
        data object OnReminderCancel : ViewEvent
    }

    sealed interface ViewAction {
        data object Back : ViewAction
        data object NavToManageFriends : ViewAction
        data object RestartApp : ViewAction
        data class ShowToast(val message: String) : ViewAction
    }
}
