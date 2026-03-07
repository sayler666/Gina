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
import coil.compose.rememberAsyncImagePainter
import com.sayler666.core.compose.Top
import com.sayler666.core.compose.conditional
import com.sayler666.core.compose.slideInVertically
import com.sayler666.core.compose.slideOutVertically
import com.sayler666.core.file.Files
import com.sayler666.core.image.ScaledBitmapInfo
import com.sayler666.core.image.scaleToMinSize
import com.sayler666.gina.attachments.viewmodel.ImagePreviewEntity
import com.sayler666.gina.attachments.viewmodel.ImagePreviewTmpEntity
import com.sayler666.gina.attachments.viewmodel.ImagePreviewTmpViewModel
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel
import com.sayler666.gina.attachments.viewmodel.ImagePreviewWithDayEntity
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.ZoomableBox

@Composable
fun ImagePreviewScreen(
    viewModel: ImagePreviewViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDayDetails: (Int) -> Unit,
) {
    val context = LocalContext.current
    StatusBarColor(color = Color.Transparent, theme = null)
    NavigationBarColor(color = Color.Transparent, theme = null)

    val imagePreview: ImagePreviewWithDayEntity? by viewModel.attachmentWithDay.collectAsStateWithLifecycle(
        null
    )
    var barsVisible by rememberSaveable { mutableStateOf(true) }

    var navigationBarVisible by remember { mutableStateOf(false) }
    fun onBackPressed() {
        navigationBarVisible = true
        onNavigateBack()
    }
    BackHandler(enabled = true) { onBackPressed() }
    if (navigationBarVisible) NavigationBarColor(theme = null)

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        imagePreview?.let {
            // scaledBitmap to fit screen
            val scaledBitmapInfo = remember(it.attachment.id) {
                it.attachment.content.scaleToMinSize()
            }

            // constraints refs
            val (zoomableBox, bottomBar, topBar) = createRefs()

            TopBar(
                barsVisible = barsVisible,
                topBarRef = topBar,
                attachmentPreviewWithDayEntity = it,
                allowNavigationToDayDetails = viewModel.allowNavigationToDayDetails,
                onBackClick = ::onBackPressed,
                onNavigateToDayDetails = onNavigateToDayDetails
            )

            ZoomablePreview(zoomableBox, scaledBitmapInfo, onClick = { barsVisible = !barsVisible })

            BottomBar(barsVisible, bottomBar, context, it)
        }
    }
}

@Composable
fun ImagePreviewTmpScreen(
    viewModel: ImagePreviewTmpViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    StatusBarColor(color = Color.Transparent, theme = null)
    NavigationBarColor(color = Color.Transparent, theme = null)

    val imagePreview: ImagePreviewTmpEntity? by viewModel.imagePreview.collectAsStateWithLifecycle(
        null
    )
    var barsVisible by rememberSaveable { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        imagePreview?.let {
            // scaledBitmap to fit screen
            val scaledBitmapInfo =
                remember(it.attachment.id) { it.attachment.content.scaleToMinSize() }

            // constraints refs
            val (zoomableBox, bottomBar) = createRefs()

            ZoomablePreview(zoomableBox, scaledBitmapInfo, onClick = { barsVisible = !barsVisible })

            BottomBar(barsVisible, bottomBar, context, it)
        }
    }
}

@Composable
private fun ConstraintLayoutScope.ZoomablePreview(
    zoomableBox: ConstrainedLayoutReference,
    scaledBitmapInfo: ScaledBitmapInfo,
    onClick: () -> Unit
) {
    ZoomableBox(
        modifier = Modifier
            .fillMaxWidth()
            .constrainAs(zoomableBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            },
        originalImageHeight = scaledBitmapInfo.height,
        originalImageWidth = scaledBitmapInfo.width,
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
    attachmentPreviewWithDayEntity: ImagePreviewWithDayEntity,
    allowNavigationToDayDetails: Boolean,
    onBackClick: () -> Unit,
    onNavigateToDayDetails: (Int) -> Unit,
) {
    AnimatedVisibility(visible = barsVisible,
        enter = slideInVertically(direction = Top),
        exit = slideOutVertically(direction = Top),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .zIndex(2f)
            .constrainAs(topBarRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }) {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
            scrolledContainerColor = Color.Transparent
        ), title = {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .conditional(allowNavigationToDayDetails) {
                        clickable {
                            onNavigateToDayDetails(attachmentPreviewWithDayEntity.dayId)
                        }
                    }
            ) {
                DayTitle(
                    attachmentPreviewWithDayEntity.dayOfMonth,
                    attachmentPreviewWithDayEntity.dayOfWeek,
                    attachmentPreviewWithDayEntity.yearAndMonth
                )
                if (allowNavigationToDayDetails)
                    Icon(
                        Icons.Filled.LocalLibrary,
                        contentDescription = "Go to day",
                        tint = MaterialTheme.colorScheme.primary
                    )
            }
        }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }, actions = {
            attachmentPreviewWithDayEntity.mood?.mapToMoodIcon()?.let { icon ->
                Icon(
                    rememberVectorPainter(icon.icon),
                    modifier = Modifier.padding(end = 16.dp),
                    tint = icon.color,
                    contentDescription = null
                )
            }
        })
    }
}

@Composable
private fun ConstraintLayoutScope.BottomBar(
    barsVisible: Boolean,
    bottomBarRef: ConstrainedLayoutReference,
    context: Context,
    imagePreviewEntity: ImagePreviewEntity
) {
    AnimatedVisibility(visible = barsVisible,
        enter = slideInVertically(),
        exit = slideOutVertically(),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .constrainAs(bottomBarRef) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }) {
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
                    bytes = imagePreviewEntity.attachment.content,
                    mimeType = imagePreviewEntity.attachment.mimeType
                )
            }) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, null)
            }
            IconButton(onClick = {
                Files.saveByteArrayToFile(
                    context = context,
                    byteArray = imagePreviewEntity.attachment.content,
                    fileName = "${imagePreviewEntity.attachment.id}.jpeg"
                )?.also {
                    Files.shareImageFile(
                        context = context,
                        file = it
                    )
                }
            }) {
                Icon(Icons.Filled.Share, null)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${imagePreviewEntity.imageFormat}: ${imagePreviewEntity.imageSize}",
                modifier = Modifier.padding(end = 16.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
