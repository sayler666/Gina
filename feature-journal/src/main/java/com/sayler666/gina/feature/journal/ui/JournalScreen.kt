package com.sayler666.gina.feature.journal.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.compose.plus
import com.sayler666.core.compose.scroll.rememberScrollConnection
import com.sayler666.core.compose.shimmerBrush
import com.sayler666.core.permission.Permissions
import com.sayler666.gina.feature.journal.viewmodel.JournalState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.EmptyState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.LoadingState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.PermissionNeededState
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewAction.NavToAttachmentPreview
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewAction.NavToDay
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewAction.NavToManageAllFilesSettings
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnAttachmentClick
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnDayClick
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnLockBottomBar
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnManageAllFilesSettingsClick
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnMoodFiltersChanged
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnRefreshPermissionStatus
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnResetFilters
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnSearchQueryChanged
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnUnlockBottomBar
import com.sayler666.gina.navigation.routes.DayDetails
import com.sayler666.gina.navigation.routes.ImagePreview
import com.sayler666.gina.navigation.routes.ImagePreviewSource
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.FiltersBar
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.ScrollIndicator
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun JournalScreen() {
    val viewModel: JournalViewModel = hiltViewModel()
    val viewState: JournalState = viewModel.viewState.collectAsStateWithLifecycle().value
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    val permissionsLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewModel.onViewEvent(OnRefreshPermissionStatus)
    }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            is NavToDay -> navigator.navigate(DayDetails(action.dayId))
            is NavToAttachmentPreview -> navigator.navigate(
                ImagePreview(action.imageId, ImagePreviewSource.Journal(action.attachmentIds))
            )

            NavToManageAllFilesSettings -> permissionsLauncher.launch(
                Permissions.getManageAllFilesSettingsIntent(context)
            )
        }
    }

    Content(
        viewState = viewState,
        viewEvent = viewModel::onViewEvent
    )
}

@Composable
private fun Content(
    viewState: JournalState,
    viewEvent: (ViewEvent) -> Unit
) {
    val hazeState = rememberHazeState()
    Box(modifier = Modifier.fillMaxSize()) {
        JournalContent(
            state = viewState,
            hazeState = hazeState,
            onViewEvent = viewEvent
        )
        Toolbar(
            viewState = viewState,
            hazeState = hazeState,
            onViewEvent = viewEvent
        )
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
private fun Toolbar(
    viewState: JournalState,
    hazeState: HazeState,
    onViewEvent: (ViewEvent) -> Unit
) {
    val searchText = rememberSaveable { mutableStateOf("") }
    FiltersBar(
        modifier = Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                backgroundColor = MaterialTheme.colorScheme.background,
                tint = HazeTint(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                )
            )
        ) {
            progressive =
                HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
        },
        hazeState = hazeState,
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
    modifier: Modifier = Modifier,
    state: JournalState,
    hazeState: HazeState,
    onViewEvent: (ViewEvent) -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .imePadding()
            .hazeSource(hazeState)
    ) {
        when (state) {
            is DaysState -> DayList(
                days = state.days,
                onViewEvent = onViewEvent,
                incognitoMode = state.incognitoMode,
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
    incognitoMode: Boolean = false,
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
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp
    val bottomPadding =
        WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + BOTTOM_NAV_HEIGHT
    val daysGrouped = days.groupBy { it.header }

    // Flat label list mirroring the LazyColumn item order: carousel, sticky headers + day rows, spacer
    val flatLabels = remember(daysGrouped) {
        buildList {
            add("")  // carousel / headerContent item
            daysGrouped.forEach { (header, days) ->
                add(header)              // sticky header
                repeat(days.size) { add(header) }  // day rows in this group
            }
            add("")  // bottom spacer
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(top = topPadding)
        ) {
            item {
                headerContent()
            }

            daysGrouped.forEach { (header, days) ->
                stickyHeader {
                    ListStickyHeader(
                        text = header,
                        hazeState = hazeState
                    )
                }

                items(
                    items = days,
                    key = { item -> item.id }
                ) { dayRowState ->
                    DayRow(
                        state = dayRowState,
                        modifier = Modifier
                            .animateItem()
                            .hazeSource(hazeState),
                        onClick = { onViewEvent(OnDayClick(dayRowState.id)) },
                        incognitoMode = incognitoMode
                    )
                }
            }
            item {
                Spacer(
                    modifier = Modifier
                        .windowInsetsBottomHeight(
                            insets = WindowInsets.systemBars + WindowInsets(bottom = BOTTOM_NAV_HEIGHT + 12.dp)
                        )
                )
            }
        }

        val layoutInfo = listState.layoutInfo
        val firstVisible = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
        val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        val visibleCount = (lastVisible - firstVisible + 1).coerceAtLeast(1)

        ScrollIndicator(
            firstVisibleItemIndex = firstVisible,
            totalItemsCount = flatLabels.size,
            visibleItemsCount = visibleCount,
            isScrollInProgress = listState.isScrollInProgress,
            scrollToItem = { listState.scrollToItem(it) },
            labelForIndex = { flatLabels.getOrElse(it) { "" } },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = bottomPadding),
        )
    }
}

@Composable
private fun AttachmentCarousel(
    state: HorizontalImagesCarouselState,
    onViewEvent: (ViewEvent) -> Unit,
) {
    val resources = LocalResources.current
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
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp
    Column(Modifier
        .fillMaxSize()
        .padding(top = topPadding)) {
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
