package com.sayler666.gina.day.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SaveFab(onSaveButtonClicked: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.29f), Color.Transparent)
                ),
                shape = CircleShape
            ),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        onClick = onSaveButtonClicked
    ) {
        Icon(Filled.Save, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun TextFormatButton(showFormat: MutableState<Boolean>) {
    IconButton(onClick = { showFormat.value = !showFormat.value }) {
        Icon(Filled.TextFormat, null)
    }
}
