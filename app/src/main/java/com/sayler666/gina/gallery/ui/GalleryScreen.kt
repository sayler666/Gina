package com.sayler666.gina.gallery.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.sayler666.gina.destinations.FullImageDialogDestination
import com.sayler666.gina.gallery.viewModel.GalleryState
import com.sayler666.gina.gallery.viewModel.GalleryState.DataState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptySearchState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptyState
import com.sayler666.gina.gallery.viewModel.GalleryViewModel
import com.sayler666.gina.ginaApp.viewModel.BottomNavigationBarViewModel
import com.sayler666.gina.ui.EmptyResult
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination
@Composable
fun GalleryScreen(
    destinationsNavigator: DestinationsNavigator,
    viewModel: GalleryViewModel = hiltViewModel(),
    bottomBarViewModel: BottomNavigationBarViewModel
) {
    val state: GalleryState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.openImage.collectLatest {
            it.let { attachment ->
                destinationsNavigator.navigate(
                    FullImageDialogDestination(
                        attachment.bytes,
                        attachment.mimeType,
                        attachment.dayId
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gallery") }) },
        content = { padding ->
            Gallery(
                padding = padding,
                state = state,
                fetchNextPage = viewModel::fetchNextPage,
                onScrollStarted = bottomBarViewModel::hide,
                onScrollEnded = bottomBarViewModel::show,
                openImage = viewModel::fetchFullImage
            )
        })
}

@Composable
private fun Gallery(
    padding: PaddingValues,
    state: GalleryState,
    fetchNextPage: () -> Unit,
    onScrollStarted: () -> Unit,
    onScrollEnded: () -> Unit,
    openImage: (Int) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        when (state) {
            is DataState -> DataState(
                state = state,
                fetchNextPage = fetchNextPage,
                onScrollStarted = onScrollStarted,
                onScrollEnded = onScrollEnded,
                openImage = openImage
            )

            EmptySearchState -> EmptyResult(
                "Empty search result!",
                "Try narrowing search criteria."
            )

            EmptyState -> EmptyResult("No data found!", "Add some attachments.")
        }
    }
}

@Composable
fun DataState(
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
        columns = GridCells.Adaptive(minSize = 90.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        items(state.images) { image ->
            Image(
                modifier = Modifier
                    .size(90.dp)
                    .padding(start = 1.dp, bottom = 1.dp)
                    .clickable { image.id?.let { openImage(it) } },
                contentScale = ContentScale.Crop,
                painter = rememberAsyncImagePainter(image.bytes),
                contentDescription = "",
            )
        }
    }
}

fun LazyGridState.isScrolledToTheEnd(offset: Int = 12): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index
        ?: 0) >= layoutInfo.totalItemsCount - 1 - offset
}

