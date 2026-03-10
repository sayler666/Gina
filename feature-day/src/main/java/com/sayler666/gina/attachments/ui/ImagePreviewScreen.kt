package com.sayler666.gina.attachments.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.compose.rememberAsyncImagePainter
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel.ViewAction.Back
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel.ViewAction.NavToDayDetails
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel.ViewEvent.OnLoadPage
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel.ViewEvent.OnNavigateToDayDetails
import com.sayler666.gina.navigation.ImagePreviewSource
import com.sayler666.gina.navigation.ImagePreviewSource.Day
import com.sayler666.gina.navigation.ImagePreviewSource.Gallery
import com.sayler666.gina.navigation.ImagePreviewSource.Journal
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.LocalSharedTransitionScope
import com.sayler666.gina.ui.ZoomableBox
import com.sayler666.gina.ui.hideSystemBars
import com.sayler666.gina.ui.showSystemBars

@Composable
fun ImagePreviewScreen(
    initialAttachmentId: Int,
    source: ImagePreviewSource,
) {
    val viewModelKey = when (source) {
        Gallery -> "gallery_$initialAttachmentId"
        is Day -> "day_${source.dayId}_$initialAttachmentId"
        is Journal -> "journal_$initialAttachmentId"
    }
    val viewModel: ImagePreviewViewModel =
        hiltViewModel<ImagePreviewViewModel, ImagePreviewViewModel.Factory>(key = viewModelKey) {
            it.create(initialAttachmentId, source)
        }
    val navigator = LocalNavigator.current
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    BackHandler(enabled = true) { viewModel.onViewEvent(OnBackPressed) }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            Back -> navigator.back()
            is NavToDayDetails -> navigator.navigate(Route.DayDetails(action.dayId))
        }
    }

    Content(
        viewState = viewState,
        initialAttachmentId = initialAttachmentId,
        source = source,
        viewEvent = viewModel::onViewEvent
    )
}

@Composable
private fun Content(
    viewState: ImagePreviewViewModel.State,
    initialAttachmentId: Int,
    source: ImagePreviewSource,
    viewEvent: (ImagePreviewViewModel.ViewEvent) -> Unit,
) {
    val context = LocalContext.current
    var barsVisible by rememberSaveable { mutableStateOf(true) }

    val window = (context as Activity).window
    LaunchedEffect(barsVisible) {
        if (barsVisible) window.showSystemBars() else window.hideSystemBars()
    }

    if (viewState.attachmentIds.isEmpty()) return

    // rememberSaveable initializes from viewState.initialPage on first load (IDs ready),
    // and restores the user's current page after a configuration change.
    var savedPage by rememberSaveable { mutableIntStateOf(viewState.initialPage) }

    val pagerState = rememberPagerState(initialPage = savedPage) {
        viewState.attachmentIds.size
    }

    LaunchedEffect(pagerState.currentPage) {
        savedPage = pagerState.currentPage
    }

    // targetPage updates as soon as the user starts swiping, giving DB load a head start.
    // Preloads current ± 1 pages.
    LaunchedEffect(pagerState.targetPage) {
        val page = pagerState.targetPage
        val ids = viewState.attachmentIds
        ids.getOrNull(page)?.let { viewEvent(OnLoadPage(it)) }
        ids.getOrNull(page - 1)?.let { viewEvent(OnLoadPage(it)) }
        ids.getOrNull(page + 1)?.let { viewEvent(OnLoadPage(it)) }
    }

    val currentPageId = viewState.attachmentIds[pagerState.currentPage]
    val currentPageData = viewState.pages[currentPageId]
    val allowNavToDayDetails = source !is Day

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (pagerRef, bottomBarRef, topBarRef) = createRefs()

        TopBar(
            barsVisible = barsVisible && currentPageData != null,
            topBarRef = topBarRef,
            attachmentPreviewWithDayEntity = currentPageData?.entity,
            allowNavigationToDayDetails = allowNavToDayDetails,
            onBackClick = { viewEvent(OnBackPressed) },
            onNavigateToDayDetails = { dayId -> viewEvent(OnNavigateToDayDetails(dayId)) }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(pagerRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                },
            key = { viewState.attachmentIds[it] }
        ) { page ->
            val pageId = viewState.attachmentIds[page]
            val pageData = viewState.pages[pageId]
            if (pageData != null) {
                ImagePreviewPage(
                    pageId = pageId,
                    initialAttachmentId = initialAttachmentId,
                    scaledBitmapInfo = pageData.bitmap,
                    onToggleBars = { barsVisible = !barsVisible }
                )
            }
        }

        BottomBar(
            barsVisible = barsVisible && currentPageData != null,
            bottomBarRef = bottomBarRef,
            context = context,
            imagePreviewEntity = currentPageData?.entity
        )
    }
}

@Composable
private fun ImagePreviewPage(
    pageId: Int,
    initialAttachmentId: Int,
    scaledBitmapInfo: com.sayler666.core.image.ScaledBitmapInfo,
    onToggleBars: () -> Unit,
) {
    val sharedScope = LocalSharedTransitionScope.current
    val sharedElementModifier: Modifier =
        if (sharedScope != null && pageId == initialAttachmentId) {
            val state = sharedScope.rememberSharedContentState("attachment_$pageId")
            val animScope = LocalNavAnimatedContentScope.current
            with(sharedScope) {
                Modifier.sharedElement(
                    sharedContentState = state,
                    animatedVisibilityScope = animScope
                )
            }
        } else Modifier

    ZoomableBox(
        modifier = Modifier
            .fillMaxSize()
            .then(sharedElementModifier),
        click = onToggleBars
    ) {
        Image(
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            ),
            painter = rememberAsyncImagePainter(scaledBitmapInfo.bitmap),
            contentDescription = null
        )
    }
}
