package com.sayler666.gina.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun EmptyResult(
    header: String,
    body: String,
    headerStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    bodyStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .padding(top = 16.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = header,
            style = headerStyle
        )
        Text(
            text = body,
            style = bodyStyle
        )
    }
}
