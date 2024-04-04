package com.sayler666.gina.ui.richeditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.sayler666.core.string.countWordsAndChars

@Composable
fun WordCharsCounter(
    text: String,
    modifier: Modifier = Modifier
) {
    val (wordCount, charCount) = remember(text) {
        val (wordCount, charCount) = text.countWordsAndChars()
        mutableIntStateOf(wordCount) to mutableIntStateOf(charCount)
    }

    Text(
        text = "${wordCount.intValue}:${charCount.intValue}",
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Right,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}
