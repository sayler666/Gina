package com.sayler666.gina.gallery.ui

import android.graphics.BitmapFactory.Options
import android.graphics.BitmapFactory.decodeByteArray
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
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import coil.compose.AsyncImage
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
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
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
    val gridState = rememberLazyStaggeredGridState()

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
                val aspectRatio: Float = remember(image.bytes) {
                    val options = Options().apply { inJustDecodeBounds = true }
                    decodeByteArray(image.bytes, 0, image.bytes.size, options)

                    if (options.outWidth > 0 && options.outHeight > 0) {
                        options.outWidth.toFloat() / options.outHeight.toFloat()
                    } else {
                        1f
                    }
                }

                AsyncImage(
                    model = image.bytes,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clickable { image.id?.let { openImage(it) } },
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

fun LazyGridState.isScrolledToTheEnd(offset: Int = 12): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index
        ?: 0) >= layoutInfo.totalItemsCount - 1 - offset
}
