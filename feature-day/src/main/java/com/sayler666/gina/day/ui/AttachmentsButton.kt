package com.sayler666.gina.day.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sayler666.core.file.handleMultipleVisualMedia
import com.sayler666.gina.resources.R

@Composable
fun rememberLauncherForMultipleImages(
    context: Context,
    onResult: (List<Pair<ByteArray, String>>) -> Unit,
) = rememberLauncherForActivityResult(PickMultipleVisualMedia()) {
    handleMultipleVisualMedia(it, context) { attachments -> onResult(attachments) }
}

@Composable
fun AttachmentsCountLabel(count: Int) {
    Text(
        text = stringResource(R.string.day_attachments_count, count),
        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttachmentsButton(onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false),
                enabled = true,
                onLongClick = { onLongClick?.invoke() },
                onClick = onClick
            )
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Icon(Filled.AddAPhoto, null)
    }
}
