package com.sayler666.gina.attachments.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.sayler666.core.compose.Top
import com.sayler666.core.compose.conditional
import com.sayler666.core.compose.slideInVertically
import com.sayler666.core.compose.slideOutVertically
import com.sayler666.gina.attachments.viewmodel.ImagePreviewWithDayEntity
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.DayDateHeader

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun ConstraintLayoutScope.TopBar(
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
                        DayDateHeader(
                            dayOfMonth = entity.dayOfMonth,
                            dayOfWeek = entity.dayOfWeek,
                            yearAndMonth = entity.yearAndMonth
                        )
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