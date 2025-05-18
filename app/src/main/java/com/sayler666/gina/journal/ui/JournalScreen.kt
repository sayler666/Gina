package com.sayler666.gina.journal.ui

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.compose.plus
import com.sayler666.core.compose.scroll.rememberScrollConnection
import com.sayler666.core.compose.shimmerBrush
import com.sayler666.gina.R
import com.sayler666.gina.attachments.ui.ImagePreviewScreenNavArgs
import com.sayler666.gina.core.permission.Permissions.getManageAllFilesSettingsIntent
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.ginaApp.BOTTOM_NAV_HEIGHT
import com.sayler666.gina.journal.viewmodel.JournalState
import com.sayler666.gina.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptyState
import com.sayler666.gina.journal.viewmodel.JournalState.LoadingState
import com.sayler666.gina.journal.viewmodel.JournalState.PermissionNeededState
import com.sayler666.gina.journal.viewmodel.JournalViewModel
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewAction
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewAction.NavToAttachmentPreview
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewAction.NavToDay
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewAction.NavToManageAllFilesSettings
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnAttachmentClick
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnDayClick
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnLockBottomBar
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnManageAllFilesSettingsClick
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnMoodFiltersChanged
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnRefreshPermissionStatus
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnResetFilters
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnSearchQueryChanged
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.journal.viewmodel.JournalViewModel.ViewEvent.OnUnlockBottomBar
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.FiltersBar
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@RootNavGraph
@Destination
@Composable
fun JournalScreen(
    destinationsNavigator: DestinationsNavigator
) {
    val viewModel: JournalViewModel = hiltViewModel()
    val viewState: JournalState = viewModel.viewState.collectAsStateWithLifecycle().value

    val permissionsLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewModel.onViewEvent(OnRefreshPermissionStatus)
    }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        onViewAction(action, destinationsNavigator, permissionsLauncher)
    }

    Scaffold(
        topBar = {
            Toolbar(
                viewState = viewState,
                onViewEvent = viewModel::onViewEvent
            )
        },
        content = { padding ->
            JournalContent(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding()),
                state = viewState,
                onViewEvent = viewModel::onViewEvent
            )
        }
    )
}

private fun onViewAction(
    action: ViewAction,
    destinationsNavigator: DestinationsNavigator,
    permissionsResult: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    when (action) {
        is NavToDay -> destinationsNavigator.navToDay(action.dayId)
        is NavToAttachmentPreview -> destinationsNavigator.navToAttachment(action.imageId)
        NavToManageAllFilesSettings -> permissionsResult.launch(getManageAllFilesSettingsIntent())
    }
}

private fun DestinationsNavigator.navToAttachment(imageId: Int) = navigate(
    ImagePreviewScreenDestination(ImagePreviewScreenNavArgs(imageId))
)

fun DestinationsNavigator.navToDay(dayId: Int) = navigate(
    DayDetailsScreenDestination(DayDetailsScreenNavArgs(dayId))
)

@Composable
@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
private fun Toolbar(
    viewState: JournalState,
    onViewEvent: (ViewEvent) -> Unit
) {
    val searchText = rememberSaveable { mutableStateOf("") }
    FiltersBar(
        title = "Gina",
        searchText = searchText.value,
        onSearchTextChanged = {
            searchText.value = it
            onViewEvent(OnSearchQueryChanged(searchText.value))
        },
        onClearClick = {
            searchText.value = ""
            onViewEvent(OnSearchQueryChanged(""))
        },
        moodFilters = if (viewState is DaysState) viewState.moods else emptyList(),
        onMoodFiltersUpdate = { moods ->
            onViewEvent(OnMoodFiltersChanged(moods))
        },
        onResetFiltersClicked = {
            onViewEvent(OnResetFilters)
        },
        filtersActive = viewState.filtersActive,
        onSearchVisibilityChanged = { show ->
            when (show) {
                true -> onViewEvent(OnLockBottomBar)
                false -> onViewEvent(OnUnlockBottomBar)
            }
        }
    )
}

@Composable
private fun JournalContent(
    state: JournalState,
    modifier: Modifier = Modifier,
    onViewEvent: (ViewEvent) -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .imePadding()
    ) {
        when (state) {
            is DaysState -> DayList(
                days = state.days,
                onViewEvent = onViewEvent,
                headerContent = @Composable {
                    AttachmentCarousel(
                        state = state.previousYearsAttachments,
                        onViewEvent = onViewEvent,
                    )
                }
            )

            is EmptySearchState -> EmptyResult(
                header = stringResource(R.string.empty_search_result_title),
                body = stringResource(R.string.empty_search_result_body)
            )

            is EmptyState -> EmptyResult(
                header = stringResource(R.string.empty_state_title),
                body = stringResource(R.string.empty_state_body)
            )

            PermissionNeededState -> FilePermissionPermissionPrompt(onViewEvent)
            LoadingState -> {}
        }
        AnimatedVisibility(
            visible = state is LoadingState,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Loading()
        }

    }
}

@Composable
private fun DayList(
    days: List<DayRowState>,
    onViewEvent: (ViewEvent) -> Unit,
    headerContent: @Composable LazyItemScope.() -> Unit
) {
    val listState = rememberLazyListState()
    val nestedScrollConnection = rememberScrollConnection(
        onScrollDown = { onViewEvent(OnHideBottomBar) },
        onScrollUp = { onViewEvent(OnShowBottomBar) }
    )

    LaunchedEffect(!listState.canScrollForward) {
        onViewEvent(OnShowBottomBar)
    }

    val hazeState = rememberHazeState()

    val daysGrouped = days.groupBy { it.header }
    LazyColumn(
        Modifier
            .nestedScroll(nestedScrollConnection),
        state = listState
    ) {
        item {
            headerContent()
        }

        daysGrouped.forEach { (header, days) ->
            stickyHeader {
                ListStickyHeader(
                    hazeState = hazeState,
                    text = header
                )
            }

            items(
                items = days,
                key = { item -> item.id }
            ) { dayRowState ->
                DayRow(
                    state = dayRowState,
                    modifier = Modifier.animateItem().hazeSource(hazeState),
                    onClick = { onViewEvent(OnDayClick(dayRowState.id)) }
                )
            }
        }
        item {
            Spacer(
                modifier = Modifier
                    .windowInsetsBottomHeight(
                        insets = WindowInsets.systemBars + WindowInsets(bottom = BOTTOM_NAV_HEIGHT)
                    )
            )
        }
    }
}

@Composable
private fun AttachmentCarousel(
    state: HorizontalImagesCarouselState,
    onViewEvent: (ViewEvent) -> Unit,
) {
    val resources = LocalContext.current.resources
    HorizontalImagesCarousel(
        state = state,
        onImageClick = { id -> onViewEvent(OnAttachmentClick(id)) },
        label = { attachment ->
            if (attachment.yearsAgo > 0) {
                resources.getQuantityString(
                    R.plurals.years_ago_label,
                    attachment.yearsAgo,
                    attachment.yearsAgo
                )
            } else {
                resources.getString(R.string.today)
            }
        }
    )
}

@Composable
private fun FilePermissionPermissionPrompt(
    onViewEvent: (ViewEvent) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = { onViewEvent(OnManageAllFilesSettingsClick) },
        ) {
            Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(R.string.select_database_grant_permission)
            )
        }
    }
}

@Composable
private fun Loading() {
    Column(Modifier.fillMaxSize()) {
        repeat(3) {
            Column(Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .size(width = 140.dp, height = 30.dp)
                        .background(shimmerBrush(targetValue = 1100f))
                )
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .size(width = 800.dp, height = 20.dp)
                        .background(shimmerBrush(targetValue = 1600f))
                )
                Box(
                    modifier = Modifier
                        .size(width = 800.dp, height = 20.dp)
                        .background(shimmerBrush(targetValue = 1600f))
                )
            }
        }
    }
}
