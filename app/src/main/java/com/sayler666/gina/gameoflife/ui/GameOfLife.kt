package com.sayler666.gina.gameoflife.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sayler666.gina.gameoflife.viewmodel.Data
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun GameOfLife(viewModel: GameOfLifeViewModel = viewModel()) {
    val uiState: Data by viewModel.generationsFlow.collectAsStateWithLifecycle()
    val primaryColor = MaterialTheme.colors.primary
    val surfaceColor = MaterialTheme.colors.surface
    Column {
        Row(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = {
                    viewModel.stop()
                },
            ) {
                Text("Stop")
            }
            Button(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    viewModel.resume()
                },
            ) {
                Text("Resume")
            }
            Button(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    viewModel.restart()
                },
            ) {
                Text("Restart")
            }
            Text(
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 8.dp),
                text = "${uiState.step}",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
                .padding(end = 16.dp)
                .padding(top = 0.dp)
        ) {
            val canvasSize = minOf(size.width, size.height)
            val space = canvasSize / uiState.resolution

            // background
            drawRect(
                color = surfaceColor, size = Size(size.width, size.width)
            )
            // grid
            for (i in 0..uiState.resolution) {
                drawLine(
                    color = Color(0x4DFFFFFF),
                    start = Offset(x = i * space, y = 0f),
                    end = Offset(x = i * space, y = canvasSize),
                )
                drawLine(
                    color = Color(0x4DFFFFFF),
                    start = Offset(y = i * space, x = 0f),
                    end = Offset(y = i * space, x = canvasSize),
                )
            }

            // active pixels
            for (x in 0 until uiState.resolution) {
                for (y in 0 until uiState.resolution) {
                    if (uiState.data[x][y]) {
                        drawRect(
                            color = primaryColor,
                            topLeft = Offset(x = x * space, y = y * space),
                            size = Size(space, space)
                        )
                    }
                }
            }
        }
    }
}
