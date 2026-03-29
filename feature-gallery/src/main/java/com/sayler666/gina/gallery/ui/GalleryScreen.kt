package com.sayler666.gina.gallery.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.compose.AsyncImage
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.compose.scroll.rememberScrollConnection
import com.sayler666.core.compose.shimmerBrush
import com.sayler666.gina.gallery.viewModel.GalleryState
import com.sayler666.gina.gallery.viewModel.GalleryState.DataState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptySearchState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptyState
import com.sayler666.gina.gallery.viewModel.GalleryState.LoadingState
import com.sayler666.gina.gallery.viewModel.GalleryViewModel
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewAction.NavigateToImage
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnFetchNextPage
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnFiltersChanged
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnImageClick
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.navigation.routes.ImagePreview
import com.sayler666.gina.navigation.routes.ImagePreviewSource
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.LocalSharedTransitionScope
import com.sayler666.gina.ui.ScrollIndicator
import com.sayler666.gina.ui.filters.FiltersBar
import com.sayler666.gina.ui.filters.FiltersState
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.sqrt

private val monthYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

@Composable
fun GalleryScreen() {
    val vmKey = rememberSaveable { UUID.randomUUID().toString() }
    val viewModel: GalleryViewModel = hiltViewModel(key = vmKey)
    val viewState: GalleryState by viewModel.viewState.collectAsStateWithLifecycle()
    val filtersState: FiltersState by viewModel.filtersState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            is NavigateToImage ->
                navigator.navigate(ImagePreview(action.id, ImagePreviewSource.Gallery))
        }
    }

    val hazeState = rememberHazeState()

    Box(modifier = Modifier.fillMaxSize()) {
        Content(
            state = viewState,
            hazeState = hazeState,
            viewEvent = viewModel::onViewEvent,
        )

        Toolbar(
            hazeState = hazeState,
            filtersState = filtersState,
            onViewEvent = viewModel::onViewEvent,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun Toolbar(
    hazeState: HazeState,
    filtersState: FiltersState,
    onViewEvent: (ViewEvent) -> Unit,
) {
    FiltersBar(
        modifier = Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                backgroundColor = MaterialTheme.colorScheme.background,
                tint = HazeTint(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
            )
        ) {
            progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
        },
        hazeState = hazeState,
        title = stringResource(R.string.gallery_label),
        filtersState = filtersState,
        onFiltersChanged = { onViewEvent(OnFiltersChanged(it)) },
    )
}

@Composable
private fun Content(
    state: GalleryState,
    hazeState: HazeState,
    viewEvent: (ViewEvent) -> Unit,
) {
    Column(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .imePadding()
            .hazeSource(hazeState)
    ) {
        when (state) {
            LoadingState -> LoadingGrid()

            is DataState -> ImagesGrid(
                state = state,
                onViewEvent = viewEvent,
            )

            EmptySearchState -> EmptyResult(
                header = stringResource(R.string.empty_search_result_title),
                body = stringResource(R.string.empty_search_result_body)
            )

            EmptyState -> EmptyResult(
                header = stringResource(R.string.empty_state_title),
                body = stringResource(R.string.gallery_empty_state_body)
            )
        }
    }
}

@Composable
private fun ImagesGrid(
    state: DataState,
    onViewEvent: (ViewEvent) -> Unit,
) {
    val gridState = rememberLazyGridState()
    var columns by rememberSaveable { mutableIntStateOf(3) }
    val maxColumns = 10
    val minColumns = 2

    LaunchedEffect(key1 = gridState, key2 = columns, block = {
        snapshotFlow { gridState.isScrolledToTheEnd((columns - 1) * 20) }.collect {
            onViewEvent(OnFetchNextPage)
        }
    })

    val nestedScrollConnection = rememberScrollConnection(
        onScrollDown = { onViewEvent(OnHideBottomBar) },
        onScrollUp = { onViewEvent(OnShowBottomBar) }
    )

    val sharedScope = LocalSharedTransitionScope.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val layoutDirection = LocalLayoutDirection.current
    val horizontalInsetsPadding = WindowInsets.safeDrawing
        .only(WindowInsetsSides.Horizontal)
        .asPaddingValues()
    val startPadding = horizontalInsetsPadding.calculateStartPadding(layoutDirection)
    val endPadding = horizontalInsetsPadding.calculateEndPadding(layoutDirection)

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(columns) {
                awaitEachGesture {
                    // Wait for first touch without consuming it (scroll must still work)
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                    var zoom = 1f
                    var prevDistance = 0f
                    do {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val pressed = event.changes.filter { it.pressed }
                        if (pressed.size >= 2) {
                            val dx = pressed[0].position.x - pressed[1].position.x
                            val dy = pressed[0].position.y - pressed[1].position.y
                            val distance = sqrt(dx * dx + dy * dy)
                            if (prevDistance > 0f) zoom *= distance / prevDistance
                            prevDistance = distance
                        } else {
                            prevDistance = 0f
                            zoom = 1f
                        }
                        when {
                            zoom > 1.3f -> {
                                columns = (columns - 1).coerceAtLeast(minColumns)
                                event.changes.forEach { it.consume() }
                                return@awaitEachGesture
                            }

                            zoom < 0.7f -> {
                                columns = (columns + 1).coerceAtMost(maxColumns)
                                event.changes.forEach { it.consume() }
                                return@awaitEachGesture
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
            state = gridState,
            columns = GridCells.Fixed(columns),
            contentPadding = WindowInsets.systemBars
                .only(WindowInsetsSides.Vertical)
                .add(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .add(WindowInsets(bottom = BOTTOM_NAV_HEIGHT + 18.dp, top = 64.dp))
                .asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(4.dp/columns),
            horizontalArrangement = Arrangement.spacedBy(4.dp/columns)
        ) {
            items(
                items = state.images
            ) { image ->
                val imageId = image.id
                val sharedModifier = if (sharedScope != null && imageId != null) {
                    val state = sharedScope.rememberSharedContentState("attachment_$imageId")
                    with(sharedScope) {
                        Modifier
                            .fillMaxWidth()
                            .sharedElement(
                                sharedContentState = state,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            )
                    }
                } else {
                    Modifier
                        .fillMaxWidth()
                }

                AsyncImage(
                    model = image.content,
                    contentDescription = null,
                    modifier = sharedModifier
                        .aspectRatio(1f)
                        .animateItem()
                        .clickable {
                            imageId?.let { onViewEvent(OnImageClick(it)) }
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }


        val layoutInfo = gridState.layoutInfo
        val firstVisible = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
        val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        val visibleCount = (lastVisible - firstVisible + 1).coerceAtLeast(1)

        ScrollIndicator(
            firstVisibleItemIndex = firstVisible,
            totalItemsCount = state.images.size,
            visibleItemsCount = visibleCount,
            isScrollInProgress = gridState.isScrollInProgress,
            scrollToItem = { gridState.scrollToItem(it) },
            labelForIndex = { index ->
                state.images.getOrNull(index)?.date?.format(monthYearFormatter) ?: ""
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = startPadding,
                    end = endPadding,
                    top = statusBarTop + 64.dp,
                    bottom = BOTTOM_NAV_HEIGHT + 64.dp
                ),
        )
    }
}

@Composable
private fun LoadingGrid() {
    val lazyGridState = rememberLazyGridState()
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyGridState,
        columns = GridCells.Fixed(3),
        contentPadding = WindowInsets.systemBars
            .only(WindowInsetsSides.Vertical)
            .add(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .add(WindowInsets(top = topPadding))
            .asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(20) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(start = 1.dp, bottom = 1.dp)
                        .background(shimmerBrush(true))
                )
            }
        }
    }
}

fun LazyGridState.isScrolledToTheEnd(offset: Int = 20): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index
        ?: 0) >= layoutInfo.totalItemsCount - 1 - offset
}
