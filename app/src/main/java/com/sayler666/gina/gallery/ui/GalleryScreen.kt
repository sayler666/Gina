package com.sayler666.gina.gallery.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.NavigationBarColor
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination
@Composable
fun GalleryScreen(
    destinationsNavigator: DestinationsNavigator,
    viewModel: GalleryViewModel = hiltViewModel(),
    ginaVM: GinaMainViewModel = hiltViewModel()
) {
    val state: GalleryState by viewModel.state.collectAsStateWithLifecycle()
    val theme by ginaVM.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme)

    LaunchedEffect(Unit) {
        viewModel.openImage.collectLatest {
            it.id?.let { imageId ->
                destinationsNavigator.navigate(
                    ImagePreviewScreenDestination(ImagePreviewScreenNavArgs(imageId))
                )
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gallery") }) },
        content = { padding ->
            Gallery(
                modifier = Modifier.padding(top = padding.calculateTopPadding()),
                state = state,
                fetchNextPage = viewModel::fetchNextPage,
                onScrollStarted = { viewModel.onViewEvent(OnHideBottomBar) },
                onScrollEnded = { viewModel.onViewEvent(OnShowBottomBar) },
                openImage = viewModel::fetchFullImage
            )
        })
}

@Composable
private fun Gallery(
    modifier: Modifier = Modifier,
    state: GalleryState,
    fetchNextPage: () -> Unit,
    onScrollStarted: () -> Unit,
    onScrollEnded: () -> Unit,
    openImage: (Int) -> Unit
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
@OptIn(ExperimentalLayoutApi::class)
private fun LoadingGrid() {
    FlowRow(Modifier.fillMaxSize()) {
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

@Composable
fun ImagesGrid(
    state: DataState,
    fetchNextPage: () -> Unit,
    onScrollStarted: () -> Unit,
    onScrollEnded: () -> Unit,
    openImage: (Int) -> Unit
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
    LazyVerticalGrid(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        state = gridState,
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom)
            .add(WindowInsets(bottom = BOTTOM_NAV_HEIGHT))
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

fun LazyGridState.isScrolledToTheEnd(offset: Int = 12): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index
        ?: 0) >= layoutInfo.totalItemsCount - 1 - offset
}

