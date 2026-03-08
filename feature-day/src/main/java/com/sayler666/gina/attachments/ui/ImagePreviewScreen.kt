package com.sayler666.gina.attachments.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.compose.rememberAsyncImagePainter
import com.sayler666.core.compose.Top
import com.sayler666.core.compose.conditional
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.compose.slideInVertically
import com.sayler666.core.compose.slideOutVertically
import com.sayler666.core.file.Files
import com.sayler666.core.image.ScaledBitmapInfo
import com.sayler666.core.image.scaleToMinSize
import com.sayler666.gina.attachments.viewmodel.ImagePreviewEntity
import com.sayler666.gina.attachments.viewmodel.ImagePreviewTmpEntity
import com.sayler666.gina.attachments.viewmodel.ImagePreviewTmpViewModel
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel.ViewAction.Back
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel.ViewAction.NavToDayDetails
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.attachments.viewmodel.ImagePreviewWithDayEntity
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.navigation.ImagePreviewSource
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.LocalSharedTransitionScope
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.ZoomableBox

@Composable
fun ImagePreviewScreen(
    initialAttachmentId: Int,
    source: ImagePreviewSource,
) {
    val viewModelKey = when (val s = source) {
        ImagePreviewSource.Gallery -> "gallery_$initialAttachmentId"
        is ImagePreviewSource.Day -> "day_${s.dayId}_$initialAttachmentId"
        is ImagePreviewSource.Journal -> "journal_$initialAttachmentId"
    }
    val viewModel: ImagePreviewViewModel =
        hiltViewModel<ImagePreviewViewModel, ImagePreviewViewModel.Factory>(key = viewModelKey) {
            it.create(initialAttachmentId, source)
        }
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    StatusBarColor(color = Color.Transparent, theme = null)
    NavigationBarColor(color = Color.Transparent, theme = null)

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var barsVisible by rememberSaveable { mutableStateOf(true) }
    var navigationBarVisible by remember { mutableStateOf(false) }

    fun onBackPressed() {
        navigationBarVisible = true
        viewModel.onViewEvent(OnBackPressed)
    }
    BackHandler(enabled = true) { onBackPressed() }
    if (navigationBarVisible) NavigationBarColor(theme = null)

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            Back -> navigator.back()
            is NavToDayDetails ->
                navigator.navigate(Route.DayDetails(action.dayId))
        }
    }

    val pagerState = rememberPagerState(initialPage = 0) {
        viewState.attachmentIds.size.coerceAtLeast(1)
    }

    // Scroll to the correct initial page once IDs are loaded
    LaunchedEffect(viewState.initialPage, viewState.attachmentIds.size) {
        if (viewState.attachmentIds.isNotEmpty() && pagerState.currentPage != viewState.initialPage) {
            pagerState.scrollToPage(viewState.initialPage)
        }
    }

    // Trigger lazy loading of current page + neighbors
    LaunchedEffect(pagerState.currentPage) {
        val ids = viewState.attachmentIds
        val cur = ids.getOrNull(pagerState.currentPage) ?: initialAttachmentId
        viewModel.onViewEvent(ImagePreviewViewModel.ViewEvent.OnLoadPage(cur))
        ids.getOrNull(pagerState.currentPage - 1)
            ?.let { viewModel.onViewEvent(ImagePreviewViewModel.ViewEvent.OnLoadPage(it)) }
        ids.getOrNull(pagerState.currentPage + 1)
            ?.let { viewModel.onViewEvent(ImagePreviewViewModel.ViewEvent.OnLoadPage(it)) }
    }

    val currentPageId =
        viewState.attachmentIds.getOrNull(pagerState.currentPage) ?: initialAttachmentId
    val currentPageData = viewState.pages[currentPageId]
    val allowNavToDayDetails = source !is ImagePreviewSource.Day

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (pagerRef, bottomBarRef, topBarRef) = createRefs()

        TopBar(
            barsVisible = barsVisible && currentPageData != null,
            topBarRef = topBarRef,
            attachmentPreviewWithDayEntity = currentPageData,
            allowNavigationToDayDetails = allowNavToDayDetails,
            onBackClick = ::onBackPressed,
            onNavigateToDayDetails = { dayId ->
                viewModel.onViewEvent(ImagePreviewViewModel.ViewEvent.OnNavigateToDayDetails(dayId))
            }
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
            key = { viewState.attachmentIds.getOrNull(it) ?: it }
        ) { page ->
            val pageId = viewState.attachmentIds.getOrNull(page) ?: initialAttachmentId
            val pageData = viewState.pages[pageId]

            if (pageData != null) {
                val scaledBitmapInfo = remember(pageId) { pageData.attachment.content.scaleToMinSize() }
                val isInitialPage = pageId == initialAttachmentId
                val sharedScope = LocalSharedTransitionScope.current
                val sharedElementModifier: Modifier = if (sharedScope != null && isInitialPage) {
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
                    modifier = Modifier.fillMaxSize().then(sharedElementModifier),
                    click = { barsVisible = !barsVisible }
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
        }

        BottomBar(barsVisible && currentPageData != null, bottomBarRef, context, currentPageData)
    }
}

@Composable
fun ImagePreviewTmpScreen(
    image: ByteArray,
    mimeType: String,
) {
    val viewModel: ImagePreviewTmpViewModel =
        hiltViewModel<ImagePreviewTmpViewModel, ImagePreviewTmpViewModel.Factory> {
            it.create(image, mimeType)
        }
    val context = LocalContext.current
    StatusBarColor(color = Color.Transparent, theme = null)
    NavigationBarColor(color = Color.Transparent, theme = null)

    val imagePreview: ImagePreviewTmpEntity? by viewModel.imagePreview.collectAsStateWithLifecycle(null)
    var barsVisible by rememberSaveable { mutableStateOf(false) }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        imagePreview?.let {
            val scaledBitmapInfo =
                remember(it.attachment.id) { it.attachment.content.scaleToMinSize() }
            val (zoomableBox, bottomBar) = createRefs()

            ZoomablePreview(zoomableBox, null, scaledBitmapInfo, onClick = { barsVisible = !barsVisible })
            BottomBar(barsVisible, bottomBar, context, it)
        }
    }
}

@Composable
private fun ConstraintLayoutScope.ZoomablePreview(
    zoomableBox: ConstrainedLayoutReference,
    attachmentId: Int?,
    scaledBitmapInfo: ScaledBitmapInfo,
    onClick: () -> Unit
) {
    val sharedScope = LocalSharedTransitionScope.current
    val sharedElementModifier: Modifier = if (sharedScope != null && attachmentId != null) {
        val state = sharedScope.rememberSharedContentState("attachment_$attachmentId")
        val animScope = LocalNavAnimatedContentScope.current
        with(sharedScope) { Modifier.sharedElement(sharedContentState = state, animatedVisibilityScope = animScope) }
    } else Modifier

    ZoomableBox(
        modifier = Modifier
            .fillMaxWidth()
            .then(sharedElementModifier)
            .constrainAs(zoomableBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            },
        click = onClick
    ) {
        Image(
            modifier = Modifier.graphicsLayer(
                scaleX = scale, scaleY = scale, translationX = offsetX, translationY = offsetY
            ),
            painter = rememberAsyncImagePainter(scaledBitmapInfo.bitmap),
            contentDescription = null
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ConstraintLayoutScope.TopBar(
    barsVisible: Boolean,
    topBarRef: ConstrainedLayoutReference,
    attachmentPreviewWithDayEntity: ImagePreviewWithDayEntity?,
    allowNavigationToDayDetails: Boolean,
    onBackClick: () -> Unit,
    onNavigateToDayDetails: (Int) -> Unit,
) {
    AnimatedVisibility(
        visible = barsVisible,
        enter = slideInVertically(direction = Top),
        exit = slideOutVertically(direction = Top),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .zIndex(2f)
            .constrainAs(topBarRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
    ) {
        attachmentPreviewWithDayEntity?.let { entity ->
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                    scrolledContainerColor = Color.Transparent
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .conditional(allowNavigationToDayDetails) {
                                clickable { onNavigateToDayDetails(entity.dayId) }
                            }
                    ) {
                        DayTitle(entity.dayOfMonth, entity.dayOfWeek, entity.yearAndMonth)
                        if (allowNavigationToDayDetails)
                            Icon(
                                Icons.Filled.LocalLibrary,
                                contentDescription = "Go to day",
                                tint = MaterialTheme.colorScheme.primary
                            )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    entity.mood?.mapToMoodIcon()?.let { icon ->
                        Icon(
                            rememberVectorPainter(icon.icon),
                            modifier = Modifier.padding(end = 16.dp),
                            tint = icon.color,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ConstraintLayoutScope.BottomBar(
    barsVisible: Boolean,
    bottomBarRef: ConstrainedLayoutReference,
    context: Context,
    imagePreviewEntity: ImagePreviewEntity?
) {
    AnimatedVisibility(
        visible = barsVisible,
        enter = slideInVertically(),
        exit = slideOutVertically(),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .constrainAs(bottomBarRef) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
    ) {
        imagePreviewEntity?.let { entity ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    Files.openFileIntent(
                        context,
                        bytes = entity.attachment.content,
                        mimeType = entity.attachment.mimeType
                    )
                }) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, null)
                }
                IconButton(onClick = {
                    Files.saveByteArrayToFile(
                        context = context,
                        byteArray = entity.attachment.content,
                        fileName = "${entity.attachment.id}.jpeg"
                    )?.also {
                        Files.shareImageFile(context = context, file = it)
                    }
                }) {
                    Icon(Icons.Filled.Share, null)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${entity.imageFormat}: ${entity.imageSize}",
                    modifier = Modifier.padding(end = 16.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
