package com.sayler666.gina.gallery.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.compose.shimmerBrush
import com.sayler666.gina.attachments.ui.ImagePreviewScreenNavArgs
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.gallery.viewModel.GalleryState
import com.sayler666.gina.gallery.viewModel.GalleryState.DataState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptySearchState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptyState
import com.sayler666.gina.gallery.viewModel.GalleryState.LoadingState
import com.sayler666.gina.gallery.viewModel.GalleryViewModel
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.gallery.viewModel.GalleryViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.ginaApp.BOTTOM_NAV_HEIGHT
import com.sayler666.gina.ui.EmptyResult
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination
@Composable
fun GalleryScreen(
    destinationsNavigator: DestinationsNavigator,
) {
    val viewModel: GalleryViewModel = hiltViewModel()
    val state: GalleryState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.openImage.collectLatest {
            it.id?.let { imageId ->
                destinationsNavigator.navigate(
                    ImagePreviewScreenDestination(ImagePreviewScreenNavArgs(imageId))
                )
            }
        }
    }

    val hazeState = rememberHazeState()

    Box(modifier = Modifier.fillMaxSize()) {
        Gallery(
            modifier = Modifier,
            state = state,
            fetchNextPage = viewModel::fetchNextPage,
            onScrollStarted = { viewModel.onViewEvent(OnHideBottomBar) },
            onScrollEnded = { viewModel.onViewEvent(OnShowBottomBar) },
            openImage = viewModel::fetchFullImage,
            hazeState = hazeState,
        )

        TopAppBar(
            title = { Text("Gallery") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        blurRadius = 24.dp,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        tint = HazeTint(
                            MaterialTheme.colorScheme.surface.copy(alpha = 1f),
                        )
                    )
                ) {
                    progressive =
                        HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.99f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                        )
                    )
                )
        )
    }
}

@Composable
private fun Gallery(
    modifier: Modifier = Modifier,
    state: GalleryState,
    fetchNextPage: () -> Unit,
    onScrollStarted: () -> Unit,
    onScrollEnded: () -> Unit,
    openImage: (Int) -> Unit,
    hazeState: HazeState
) {
    Column(
        modifier
            .fillMaxSize()
            .imePadding()
    ) {
        when (state) {
            LoadingState -> LoadingGrid()

            is DataState -> ImagesGrid(
                state = state,
                hazeState = hazeState,
                fetchNextPage = fetchNextPage,
                onScrollStarted = onScrollStarted,
                onScrollEnded = onScrollEnded,
                openImage = openImage
            )

            EmptySearchState -> EmptyResult(
                header = "Empty search result!",
                body = "Try narrowing search criteria."
            )

            EmptyState -> EmptyResult(
                header = "No data found!",
                body = "Add some attachments."
            )
        }
    }
}

@Composable
fun ImagesGrid(
    state: DataState,
    fetchNextPage: () -> Unit,
    onScrollStarted: () -> Unit,
    onScrollEnded: () -> Unit,
    openImage: (Int) -> Unit,
    hazeState: HazeState
) {
    val gridState = rememberLazyGridState()

    LaunchedEffect(key1 = gridState, block = {
        snapshotFlow { gridState.isScrolledToTheEnd() }.collect {
            fetchNextPage()
        }
    })

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta > 30) onScrollEnded()
                if (delta < -20) onScrollStarted()
                return Offset.Zero
            }
        }
    }

    Column(
        Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .hazeSource(hazeState),
            state = gridState,
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = WindowInsets.systemBars
                .only(WindowInsetsSides.Bottom + WindowInsetsSides.Top)
                .add(WindowInsets(bottom = BOTTOM_NAV_HEIGHT, top = 64.dp))
                .asPaddingValues()
        ) {
            items(state.images) { image ->
                Image(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(start = 1.dp, bottom = 1.dp)
                        .clickable { image.id?.let { openImage(it) } },
                    contentScale = ContentScale.Crop,
                    painter = rememberAsyncImagePainter(model = image.bytes),
                    contentDescription = "",
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
            .padding(top = 90.dp)
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

fun LazyGridState.isScrolledToTheEnd(offset: Int = 12): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index
        ?: 0) >= layoutInfo.totalItemsCount - 1 - offset
}

