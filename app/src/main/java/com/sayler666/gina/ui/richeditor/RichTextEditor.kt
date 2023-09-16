package com.sayler666.gina.ui.richeditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import com.sayler666.core.compose.conditional
import com.sayler666.core.html.containsHtml
import com.sayler666.core.html.getTextWithoutHtml
import com.sayler666.gina.quotes.db.Quote

@Composable
fun RichTextEditor(
    richTextState: RichTextState,
    text: String,
    autoFocus: Boolean = false,
    quote: Quote? = null,
    onContentChanged: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    if (autoFocus) LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }
    LaunchedEffect(textFieldValue.text) {
        richTextState.setTextOrHtml(textFieldValue.text)
    }

    LaunchedEffect(richTextState.annotatedString) { onContentChanged(richTextState.toHtml()) }

    BasicRichTextEditor(state = richTextState,
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxSize()
            .conditional(autoFocus) { focusRequester(focusRequester) },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface),
        cursorBrush = SolidColor(colorScheme.primary),
        decorationBox = { innerTextField ->
            QuoteWithAuthor(
                quote = quote,
                visible = richTextState.toHtml().getTextWithoutHtml()
                    .isEmpty() && quote != null,
                onClick = { quote ->
                    with(richTextState) {
                        setHtml(quote.toHtml())
                        removeSpanStyle(currentSpanStyle)
                        removeParagraphStyle(currentParagraphStyle)
                    }
                }
            )
            innerTextField()
        }
    )
}

private fun Quote.toHtml() = "<p style=\"text-align: center;\"><i>$quote<i></p>" +
        "<p style=\"text-align: center;\"><b>—$author</b></p>" +
        "<p><br/></p>"

@Preview(backgroundColor = 0xFF009688)
@Composable
fun QuotePreview() {
    QuoteWithAuthor(
        quote = Quote(
            id = null,
            quote = "Lorem ipsum et cetera, lorem et ipum, lorem et ipum.",
            author = "sayler",
            date = 100L
        ),
        visible = true,
        onClick = {}
    )
}

@Composable
private fun QuoteWithAuthor(
    quote: Quote?,
    visible: Boolean,
    onClick: (Quote) -> Unit
) {
    var blurEnabled by remember { mutableStateOf(true) }
    val blurRadius: Dp by animateDpAsState(if (blurEnabled) 30.dp else 0.dp, tween(500))

    val infiniteTransition = rememberInfiniteTransition()
    val offsetTarget = 3000f
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = offsetTarget,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "gradient offset"
    )
    val gradient = listOf(
        colorScheme.onSurface.copy(alpha = 0.6f),
        Color.White.copy(alpha = 0.8f),
        colorScheme.onSurface.copy(alpha = 0.6f)
        )
    val brush = remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val times = offsetTarget / size.width
                return LinearGradientShader(
                    colors = gradient,
                    from = Offset(offset - size.width, offset),
                    to = Offset(
                        size.width * times + offset - size.width,
                        size.width * times + offset
                    ),
                    tileMode = TileMode.Repeated,
                    colorStops = listOf(0.05f, 0.1f, 0.15f)
                )
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (quote != null) {
            blurEnabled = false
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(blurRadius)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(quote) },
                    text = quote.quote,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        brush = brush
                    )
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(quote) },
                    text = "—${quote.author}",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Normal,
                    color = colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun RichTextState.setTextOrHtml(text: String) =
    if (text.containsHtml()) setHtml(text) else setText(text)
