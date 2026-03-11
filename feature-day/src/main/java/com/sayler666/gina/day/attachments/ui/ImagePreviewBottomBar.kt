package com.sayler666.gina.day.attachments.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.sayler666.core.compose.slideInVertically
import com.sayler666.core.compose.slideOutVertically
import com.sayler666.core.file.Files
import com.sayler666.gina.day.attachments.viewmodel.ImagePreviewEntity

@Composable
internal fun ConstraintLayoutScope.BottomBar(
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
