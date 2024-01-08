package com.sayler666.gina.attachments.ui

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.core.compose.Top
import com.sayler666.core.compose.conditional
import com.sayler666.core.compose.slideInVertically
import com.sayler666.core.compose.slideOutVertically
import com.sayler666.core.file.Files
import com.sayler666.core.image.ScaledBitmapInfo
import com.sayler666.core.image.scaleToMinSize
import com.sayler666.gina.appDestination
import com.sayler666.gina.attachments.viewmodel.ImagePreviewEntity
import com.sayler666.gina.attachments.viewmodel.ImagePreviewTmpEntity
import com.sayler666.gina.attachments.viewmodel.ImagePreviewTmpViewModel
import com.sayler666.gina.attachments.viewmodel.ImagePreviewViewModel
import com.sayler666.gina.attachments.viewmodel.ImagePreviewWithDayEntity
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.destinations.ImagePreviewTmpScreenDestination
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.StatusBarColor
import com.sayler666.gina.ui.ZoomableBox
import com.sayler666.gina.mood.ui.mapToMoodIcon

object ImagePreviewTransitions : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition()
            : EnterTransition? = when (targetState.appDestination()) {
        ImagePreviewScreenDestination -> scaleIn(animationSpec = tween(ANIMATION_DURATION)) + fadeIn()
        ImagePreviewTmpScreenDestination -> scaleIn(animationSpec = tween(ANIMATION_DURATION)) + fadeIn()

        else -> null
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition()
            : ExitTransition? = when (initialState.appDestination()) {
        ImagePreviewScreenDestination -> scaleOut(animationSpec = tween(ANIMATION_DURATION)) + fadeOut()
        ImagePreviewTmpScreenDestination -> scaleOut(animationSpec = tween(ANIMATION_DURATION)) + fadeOut()

        else -> null
    }
}

data class ImagePreviewScreenNavArgs(
    val attachmentId: Int,
    val allowNavigationToDayDetails: Boolean = true
)

@Destination(
    navArgsDelegate = ImagePreviewScreenNavArgs::class,
    style = ImagePreviewTransitions::class
)
@Composable
fun ImagePreviewScreen(
    destinationsNavigator: DestinationsNavigator,
    viewModel: ImagePreviewViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    StatusBarColor(color = Color.Transparent, theme = null)
    NavigationBarColor(color = Color.Transparent, theme = null)

    val imagePreview: ImagePreviewWithDayEntity? by viewModel.attachmentWithDay.collectAsStateWithLifecycle(
        null
    )
    var barsVisible by rememberSaveable { mutableStateOf(true) }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        imagePreview?.let {
            // scaledBitmap to fit screen
            val scaledBitmapInfo = remember(it.attachment.id) {
                it.attachment.bytes.scaleToMinSize()
            }

            // constraints refs
            val (zoomableBox, bottomBar, topBar) = createRefs()

            TopBar(
                barsVisible,
                topBar,
                it,
                destinationsNavigator,
                viewModel.allowNavigationToDayDetails
            )

            ZoomablePreview(zoomableBox, scaledBitmapInfo, onClick = { barsVisible = !barsVisible })

            BottomBar(barsVisible, bottomBar, destinationsNavigator, context, it)
        }
    }
}

data class ImagePreviewTmpScreenNavArgs(
    val image: ByteArray,
    val mimeType: String
)

@Destination(
    navArgsDelegate = ImagePreviewTmpScreenNavArgs::class,
    style = ImagePreviewTransitions::class
)
@Composable
fun ImagePreviewTmpScreen(
    destinationsNavigator: DestinationsNavigator,
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
                remember(it.attachment.id) { it.attachment.bytes.scaleToMinSize() }

            // constraints refs
            val (zoomableBox, bottomBar) = createRefs()

            ZoomablePreview(zoomableBox, scaledBitmapInfo, onClick = { barsVisible = !barsVisible })

            BottomBar(barsVisible, bottomBar, destinationsNavigator, context, it)
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
    destinationsNavigator: DestinationsNavigator,
    allowNavigationToDayDetails: Boolean
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
                            destinationsNavigator.navigate(
                                DayDetailsScreenDestination(
                                    DayDetailsScreenNavArgs(
                                        attachmentPreviewWithDayEntity.dayId
                                    )
                                )
                            )
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
            IconButton(onClick = { destinationsNavigator.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
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
    destinationsNavigator: DestinationsNavigator,
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
                destinationsNavigator.popBackStack()
                Files.openFileIntent(
                    context,
                    bytes = imagePreviewEntity.attachment.bytes,
                    mimeType = imagePreviewEntity.attachment.mimeType
                )
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
