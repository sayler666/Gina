package com.sayler666.gina.feature.journal.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.compose.plus
import com.sayler666.core.compose.scroll.rememberScrollConnection
import com.sayler666.core.compose.shimmerBrush
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.feature.journal.viewmodel.JournalState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.EmptyState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.LoadingState
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewAction.NavToAttachmentPreview
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewAction.NavToDay
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewAction.NavToDayEdit
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnAttachmentClick
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnCardAttachmentClick
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnDayClick
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnDaySwipeToEdit
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnFiltersChanged
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.feature.journal.viewmodel.JournalViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.navigation.routes.DayDetails
import com.sayler666.gina.navigation.routes.DayDetailsEdit
import com.sayler666.gina.navigation.routes.ImagePreview
import com.sayler666.gina.navigation.routes.ImagePreviewSource
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.LocalHapticFeedbackManager
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.ScrollIndicator
import com.sayler666.gina.ui.filters.FiltersBar
import com.sayler666.gina.ui.filters.FiltersState
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import com.sayler666.gina.ui.theme.GinaTheme
import com.sayler666.gina.ui.theme.Theme
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun JournalScreen() {
    val viewModel: JournalViewModel = hiltViewModel()
    val viewState: JournalState = viewModel.viewState.collectAsStateWithLifecycle().value
    val filtersState: FiltersState = viewModel.filtersState.collectAsStateWithLifecycle().value
    val navigator = LocalNavigator.current
    val haptics = LocalHapticFeedbackManager.current

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            is NavToDay -> {
                haptics.tap()
                navigator.navigate(DayDetails(action.dayId))
            }

            is NavToDayEdit -> {
                haptics.swipe()
                navigator.navigate(DayDetailsEdit(action.dayId))
            }

            is NavToAttachmentPreview -> navigator.navigate(
                ImagePreview(action.imageId, ImagePreviewSource.Journal(action.attachmentIds))
            )
        }
    }

    Content(
        viewState = viewState,
        filtersState = filtersState,
        viewEvent = viewModel::onViewEvent,
        loadImage = viewModel::loadAttachmentBytes
    )
}

@Composable
private fun Content(
    viewState: JournalState,
    filtersState: FiltersState,
    viewEvent: (ViewEvent) -> Unit,
    loadImage: suspend (Int) -> ByteArray?
) {
    val hazeState = rememberHazeState()
    Box(modifier = Modifier.fillMaxSize()) {
        JournalContent(
            state = viewState,
            hazeState = hazeState,
            onViewEvent = viewEvent,
            loadImage = loadImage
        )
        Toolbar(
            filtersState = filtersState,
            hazeState = hazeState,
            onViewEvent = viewEvent
        )
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
private fun Toolbar(
    filtersState: FiltersState,
    hazeState: HazeState,
    onViewEvent: (ViewEvent) -> Unit
) {
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
        filtersState = filtersState,
        onFiltersChanged = { onViewEvent(OnFiltersChanged(it)) },
    )
}

@Composable
private fun JournalContent(
    modifier: Modifier = Modifier,
    state: JournalState,
    hazeState: HazeState,
    onViewEvent: (ViewEvent) -> Unit,
    loadImage: suspend (Int) -> ByteArray?
) {
    Column(
        modifier
            .fillMaxSize()
            .imePadding()
            .hazeSource(hazeState)
    ) {

        AnimatedContent(
            targetState = state,
            contentKey = { it::class },
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { currentState ->
            when (currentState) {
                is DaysState -> DayList(
                    days = currentState.days,
                    onViewEvent = onViewEvent,
                    loadImage = loadImage,
                    headerContent = @Composable {
                        AttachmentCarousel(
                            state = currentState.previousYearsAttachments,
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

                LoadingState -> Loading()
            }
        }
    }
}

@Composable
private fun DayList(
    days: ImmutableList<DayRowState>,
    onViewEvent: (ViewEvent) -> Unit,
    loadImage: suspend (Int) -> ByteArray?,
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
    val layoutDirection = LocalLayoutDirection.current
    val horizontalInsetsPadding = WindowInsets.safeDrawing
        .only(WindowInsetsSides.Horizontal)
        .asPaddingValues()
    val startPadding = horizontalInsetsPadding.calculateStartPadding(layoutDirection)
    val endPadding = horizontalInsetsPadding.calculateEndPadding(layoutDirection)
    val daysGrouped = days.groupBy { it.header }

    val flatLabels = remember(daysGrouped) {
        buildList {
            add("")
            daysGrouped.forEach { (header, days) ->
                add(header)
                repeat(days.size) { add(header) }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                start = startPadding,
                end = endPadding,
                top = topPadding,
            )
        ) {
            item(key = "header") {
                headerContent()
            }

            daysGrouped.forEach { (header, days) ->
                stickyHeader(key = "sticky_$header") {
                    ListStickyHeader(
                        text = header,
                        hazeState = hazeState
                    )
                }

                itemsIndexed(
                    items = days,
                    key = { _, item -> item.id }
                ) { index, dayRowState ->
                    DayRow(
                        state = dayRowState,
                        modifier = Modifier
                            .animateItem()
                            .hazeSource(hazeState),
                        onClick = { onViewEvent(OnDayClick(dayRowState.id)) },
                        onSwipeToEdit = { onViewEvent(OnDaySwipeToEdit(dayRowState.id)) },
                        onAttachmentClick = { id, allIds ->
                            onViewEvent(OnCardAttachmentClick(id, allIds))
                        },
                        loadImage = loadImage,
                        top = index == 0,
                        bottom = index == days.size - 1
                    )
                }
            }
            item(key = "spacer") {
                Spacer(
                    modifier = Modifier
                        .windowInsetsBottomHeight(
                            insets = WindowInsets.systemBars + WindowInsets(bottom = BOTTOM_NAV_HEIGHT + 12.dp)
                        )
                )
            }
        }

        val visibleRange by remember {
            derivedStateOf {
                val items = listState.layoutInfo.visibleItemsInfo
                val first = items.firstOrNull()?.index ?: 0
                val last = items.lastOrNull()?.index ?: 0
                first..last
            }
        }
        ScrollIndicator(
            firstVisibleItemIndex = visibleRange.first,
            totalItemsCount = flatLabels.size,
            visibleItemsCount = visibleRange.count(),
            isScrollInProgress = listState.isScrollInProgress,
            scrollToItem = { listState.scrollToItem(it) },
            labelForIndex = { flatLabels.getOrElse(it) { "" } },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = startPadding,
                    end = endPadding,
                    top = topPadding,
                    bottom = bottomPadding
                ),
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
private fun Loading() {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = topPadding)
    ) {
        // Carousel skeleton — matches HorizontalImagesCarousel (120dp height, 120*1.61f width per item)
        Row(modifier = Modifier.padding(start = 14.dp, bottom = 4.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp, bottom = 4.dp)
                        .size(width = 190.dp, height = 115.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(shimmerBrush(targetValue = 1600f))
                )
            }
        }
        // Sticky header skeleton — matches ListStickyHeader padding
        Box(
            modifier = Modifier
                .padding(start = 14.dp, top = 4.dp, bottom = 6.dp)
                .size(width = 80.dp, height = 18.dp)
                .background(shimmerBrush(targetValue = 1100f))
        )
        // DayRow skeletons — matches card padding, content structure and corner radius
        val corner = 8.dp
        repeat(6) { index ->
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = corner, topEnd = corner)
                5 -> RoundedCornerShape(bottomStart = corner, bottomEnd = corner)
                else -> RoundedCornerShape(0.dp)
            }
            Column(
                Modifier
                    .padding(horizontal = 14.dp, vertical = 1.dp)
                    .fillMaxWidth()
                    .clip(shape)
                    .background(shimmerBrush(targetValue = 1600f))
                    .padding(horizontal = 12.dp)
                    .padding(top = 6.dp, bottom = 14.dp)
            ) {
                Box(
                    Modifier
                        .padding(bottom = 8.dp)
                        .size(width = 130.dp, height = 100.dp)
                )
            }
        }
    }
}

private val previewDays = persistentListOf(
    DayRowState(
        id = 1,
        dayOfMonth = "14",
        dayOfWeek = "Mon",
        yearAndMonth = "March 2025",
        header = "March 2025",
        contentPreview = "Today was a really productive day. Went for a walk in the morning and finished the new feature.",
        searchQuery = "",
        mood = Mood.GOOD
    ),
    DayRowState(
        id = 2,
        dayOfMonth = "10",
        dayOfWeek = "Thu",
        yearAndMonth = "March 2025",
        header = "March 2025",
        contentPreview = "A quiet day at home, read a book and cooked something new for dinner.",
        searchQuery = "",
        mood = Mood.NEUTRAL
    ),
    DayRowState(
        id = 3,
        dayOfMonth = "28",
        dayOfWeek = "Fri",
        yearAndMonth = "February 2025",
        header = "February 2025",
        contentPreview = "Rough day, nothing seemed to go right.",
        searchQuery = "",
        mood = Mood.BAD
    )
)

@Preview
@Composable
private fun JournalScreenDaysPreview() {
    GinaTheme(Theme.Firewatch, darkTheme = true) {
        Surface {
            Content(
                viewState = DaysState(days = previewDays),
                filtersState = FiltersState(),
                viewEvent = {},
                loadImage = { null }
            )
        }
    }
}

@Preview
@Composable
private fun JournalScreenEmptyPreview() {
    GinaTheme(Theme.Firewatch, darkTheme = true) {
        Surface {
            Content(
                viewState = EmptyState,
                filtersState = FiltersState(),
                viewEvent = {},
                loadImage = { null }
            )
        }
    }
}

@Preview
@Composable
private fun JournalScreenLoadingPreview() {
    GinaTheme(Theme.Firewatch, darkTheme = true) {
        Surface {
            Content(
                viewState = LoadingState,
                filtersState = FiltersState(),
                viewEvent = {},
                loadImage = { null }
            )
        }
    }
}
