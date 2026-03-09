package com.sayler666.gina.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString

private fun scrambleText(text: String, shift: Int): String =
    text.map { char ->
        when (char) {
            in 'a'..'z' -> 'a' + (char - 'a' + shift) % 26
            in 'A'..'Z' -> 'A' + (char - 'A' + shift) % 26
            else -> char
        }
    }.joinToString("")

@Composable
fun CaesarCipherText(
    modifier: Modifier = Modifier,
    text: String,
    shift: Int = 13,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    CaesarCipherText(
        text = AnnotatedString(text),
        shift = shift,
        modifier = modifier,
        style = style,
        color = color
    )
}

@Composable
fun CaesarCipherText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    shift: Int = 13,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val scrambled = remember(text, shift) {
        val plainScrambled = scrambleText(text.text, shift)
        buildAnnotatedString {
            append(plainScrambled)
            text.spanStyles.forEach {
                addStyle(it.item, it.start, it.end)
            }
        }
    }

    Text(
        text = scrambled,
        modifier = modifier,
        style = style,
        color = color
    )
}
