package com.sayler666.gina.gameoflife.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.gameoflife.viewmodel.Data
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Destination
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun GameOfLifeScreen(
    navController: NavController
) {
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavGraphs.root.route)
    }
    val viewModel: GameOfLifeViewModel = hiltViewModel(backStackEntry)
    val uiState: Data by viewModel.generationsFlow.collectAsStateWithLifecycle()
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
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
