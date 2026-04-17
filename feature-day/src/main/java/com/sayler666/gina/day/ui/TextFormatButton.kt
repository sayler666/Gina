package com.sayler666.gina.day.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun TextFormatButton(showFormat: MutableState<Boolean>) {
    IconButton(onClick = { showFormat.value = !showFormat.value }) {
        Icon(Icons.Filled.TextFormat, null)
    }
}
