package com.sayler666.gina.ginaApp.navigation

import android.widget.Toast
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AddDayFab() {
    val context = LocalContext.current
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape,
        onClick = {
            Toast.makeText(context, "Add new entry", Toast.LENGTH_SHORT).show()
        }) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Add new entry",
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
