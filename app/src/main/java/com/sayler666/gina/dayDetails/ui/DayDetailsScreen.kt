package com.sayler666.gina.dayDetails.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sayler666.gina.core.file.Files.openFileIntent
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel
import com.sayler666.gina.dayDetails.viewmodel.DayWithAttachmentsEntity
import com.sayler666.gina.destinations.FullImageDestination
import com.sayler666.gina.ui.ZoomableBox


data class DayDetailsScreenNavArgs(
    val dayId: Int
)

//@DayDetailsNavGraph(start = true)
@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination(
    navArgsDelegate = DayDetailsScreenNavArgs::class
)
@Composable
fun DayDetailsScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    viewModel: DayDetailsViewModel = hiltViewModel()
) {
    val day: DayWithAttachmentsEntity? by viewModel.day.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { day?.let { Text(text = it.title) } },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                day?.let {
                    if (it.attachments.isNotEmpty()) {
                        FlowRow(modifier = Modifier.padding(16.dp, 0.dp)) {
                            it.attachments.forEach { attachment ->
                                when (attachment) {
                                    is Image -> ImagePreview(
                                        destinationsNavigator,
                                        attachment
                                    )
                                    is NonImage -> FilePreview(
                                        attachment
                                    )
                                }

                            }
                        }
                    }
                    Row(modifier = Modifier.padding(16.dp, 8.dp)) {
                        Text(
                            text = it.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        })
}

@Composable
private fun ImagePreview(
    destinationsNavigator: DestinationsNavigator,
    attachment: Image
) {
    Card(
        Modifier
            .size(70.dp)
            .padding(end = 8.dp, bottom = 8.dp)
            .clickable { destinationsNavigator.navigate(FullImageDestination(attachment.byte)) }
    ) {
        Image(
            contentScale = ContentScale.Crop,
            painter = rememberAsyncImagePainter(attachment.byte),
            contentDescription = "",
        )
    }
}

@Composable
private fun FilePreview(attachment: NonImage) {
    val context = LocalContext.current
    Card(
        Modifier
            .size(70.dp)
            .padding(end = 8.dp, bottom = 8.dp)
            .clickable { openFileIntent(context, attachment.byte, attachment.mimeType) },
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Box(Modifier.padding(8.dp)) {
            Icon(
                imageVector = Icons.Outlined.FileOpen,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                contentDescription = ""
            )
            Text(
                attachment.displayName,
                modifier = Modifier.align(BottomEnd),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun FullImage(destinationsNavigator: DestinationsNavigator, image: ByteArray) {
    ZoomableBox(
        outsideImageClick = { destinationsNavigator.popBackStack() }) {
        Image(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            painter = rememberAsyncImagePainter(image),
            contentDescription = null
        )
    }
}
