package com.sayler666.gina.ui.richeditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FormatAlignCenter
import androidx.compose.material.icons.outlined.FormatAlignLeft
import androidx.compose.material.icons.outlined.FormatAlignRight
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatClear
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState

@Composable
fun RichTextStyleRow(
    modifier: Modifier = Modifier,
    state: RichTextState,
    showFormatRow: MutableState<Boolean>
) {
    AnimatedVisibility(
        visible = showFormatRow.value,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 80, easing = FastOutLinearInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 80, easing = FastOutLinearInEasing)
        )
    ) {
        Row(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .fillMaxWidth()
        ) {
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.weight(1f),
            ) {
                item {
                    RichTextStyleButton(
                        onClick = {
                            state.addParagraphStyle(
                                ParagraphStyle(
                                    textAlign = TextAlign.Left,
                                )
                            )
                        },
                        isSelected = state.currentParagraphStyle.textAlign == TextAlign.Left,
                        icon = Icons.Outlined.FormatAlignLeft
                    )
                }

                item {
                    RichTextStyleButton(
                        onClick = {
                            state.addParagraphStyle(
                                ParagraphStyle(
                                    textAlign = TextAlign.Center
                                )
                            )
                        },
                        isSelected = state.currentParagraphStyle.textAlign == TextAlign.Center,
                        icon = Icons.Outlined.FormatAlignCenter
                    )
                }

                item {
                    RichTextStyleButton(
                        onClick = {
                            state.addParagraphStyle(
                                ParagraphStyle(
                                    textAlign = TextAlign.Right
                                )
                            )
                        },
                        isSelected = state.currentParagraphStyle.textAlign == TextAlign.Right,
                        icon = Icons.Outlined.FormatAlignRight
                    )
                }

                item {
                    RichTextStyleButton(
                        onClick = {
                            state.toggleSpanStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                        icon = Icons.Outlined.FormatBold
                    )
                }

                item {
                    RichTextStyleButton(
                        onClick = {
                            state.toggleSpanStyle(
                                SpanStyle(
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        },
                        isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                        icon = Icons.Outlined.FormatItalic
                    )
                }

                item {
                    RichTextStyleButton(
                        onClick = {
                            state.toggleSpanStyle(
                                SpanStyle(
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        },
                        isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
                        icon = Icons.Outlined.FormatUnderlined
                    )
                }

                item {
                    RichTextStyleButton(
                        onClick = {
                            state.toggleSpanStyle(
                                SpanStyle(
                                    textDecoration = TextDecoration.LineThrough
                                )
                            )
                        },
                        isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
                        icon = Icons.Outlined.FormatStrikethrough
                    )
                }

                item {
                    RichTextStyleButton(
                        onClick = {
                            state.toggleSpanStyle(
                                SpanStyle(
                                    fontSize = 19.sp
                                )
                            )
                        },
                        isSelected = state.currentSpanStyle.fontSize == 19.sp,
                        icon = Icons.Outlined.FormatSize
                    )
                }
                item {
                    RichTextStyleButton(
                        onClick = {
                            state.toggleSpanStyle(
                                SpanStyle(
                                    fontSize = 23.sp
                                )
                            )
                        },
                        isSelected = state.currentSpanStyle.fontSize == 23.sp,
                        icon = Icons.Outlined.FormatSize
                    )
                }

                item {
                    Box(
                        Modifier
                            .height(24.dp)
                            .width(1.dp)
                            .background(Color(0xFF393B3D))
                    )
                }

                item {
                    RichTextStyleButton(
                        onClick = {
                            state.removeSpanStyle(state.currentSpanStyle)
                            state.removeParagraphStyle(state.currentParagraphStyle)
                        },
                        isSelected = false,
                        icon = Icons.Outlined.FormatClear,
                    )
                }

                item {
                    Box(
                        Modifier
                            .height(24.dp)
                            .width(1.dp)
                            .background(Color(0xFF393B3D))
                    )
                }
            }
            RichTextStyleButton(
                onClick = { showFormatRow.value = false },
                isSelected = false,
                icon = Icons.Outlined.Close,
            )
        }
    }
}
