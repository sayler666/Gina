package com.sayler666.gina.gallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
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
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnImageClick
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.navigation.routes.ImagePreview
import com.sayler666.gina.navigation.routes.ImagePreviewSource
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.LocalSharedTransitionScope
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val viewState: GalleryState by viewModel.viewState.collectAsStateWithLifecycle()
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

        TopAppBar(
            title = { Text(stringResource(R.string.gallery_label)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .hazeEffect(
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
                }

        )
    }
}

@Composable
private fun Content(
    state: GalleryState,
    hazeState: HazeState,
    viewEvent: (ViewEvent) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        when (state) {
            LoadingState -> LoadingGrid()

            is DataState -> ImagesGrid(
                state = state,
                hazeState = hazeState,
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
    hazeState: HazeState
) {
    val gridState = rememberLazyStaggeredGridState()

    LaunchedEffect(key1 = gridState, block = {
        snapshotFlow { gridState.isScrolledToTheEnd() }.collect {
            onViewEvent(OnFetchNextPage)
        }
    })

    val nestedScrollConnection = rememberScrollConnection(
        onScrollDown = { onViewEvent(OnHideBottomBar) },
        onScrollUp = { onViewEvent(OnShowBottomBar) }
    )

    val sharedScope = LocalSharedTransitionScope.current

    Column(
        Modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
                .hazeSource(state = hazeState),
            state = gridState,
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = WindowInsets.systemBars
                .only(WindowInsetsSides.Vertical)
                .add(WindowInsets(bottom = BOTTOM_NAV_HEIGHT, top = 64.dp))
                .asPaddingValues(),
            verticalItemSpacing = 2.dp,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(
                items = state.images,
                key = { it.id ?: it.hashCode() },
                contentType = { "image_cell" }
            ) { image ->
                val imageId = image.id
                val sharedModifier = if (sharedScope != null && imageId != null) {
                    val state = sharedScope.rememberSharedContentState("attachment_$imageId")
                    with(sharedScope) {
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(image.aspectRatio)
                            .sharedElement(
                                sharedContentState = state,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            )
                    }
                } else {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(image.aspectRatio)
                }

                AsyncImage(
                    model = image.content,
                    contentDescription = null,
                    modifier = sharedModifier.clickable {
                        imageId?.let { onViewEvent(OnImageClick(it)) }
                    },
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Composable
private fun LoadingGrid() {
    FlowRow(
        Modifier
            .fillMaxSize()
            .padding(top = 120.dp)
    ) {
        repeat(18) {
            Box(
                Modifier
                    .size(120.dp)
                    .padding(start = 1.dp, bottom = 1.dp)
                    .background(shimmerBrush(true))
            )
        }
    }
}

fun LazyStaggeredGridState.isScrolledToTheEnd(offset: Int = 12): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index
        ?: 0) >= layoutInfo.totalItemsCount - 1 - offset
}
