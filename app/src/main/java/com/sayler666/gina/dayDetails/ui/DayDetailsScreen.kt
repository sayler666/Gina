package com.sayler666.gina.dayDetails.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.core.file.Files
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel
import com.sayler666.gina.dayDetails.viewmodel.DayWithAttachmentsEntity
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.destinations.FullImageDestination


data class DayDetailsScreenNavArgs(
    val dayId: Int
)

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
    val context = LocalContext.current
    val day: DayWithAttachmentsEntity? by viewModel.day.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { day?.let { Text(text = it.date) } },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        day?.let { destinationsNavigator.navigate(DayDetailsEditScreenDestination(it.id)) }
                    }) {
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
                                        attachment,
                                        onClick = {
                                            destinationsNavigator.navigate(
                                                FullImageDestination(attachment.byte)
                                            )
                                        }
                                    )
                                    is NonImage -> FilePreview(
                                        attachment,
                                        onClick = {
                                            Files.openFileIntent(
                                                context,
                                                attachment.byte,
                                                attachment.mimeType
                                            )
                                        }
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
