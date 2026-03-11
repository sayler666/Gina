package com.sayler666.gina.gameoflife.ui

import android.graphics.RuntimeShader
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewAction.Back
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewEvent.OnRestartPressed
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewEvent.OnResumePressed
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewModel.ViewEvent.OnStopPressed
import com.sayler666.gina.gameoflife.viewmodel.GameOfLifeViewState
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.LocalNavigator
import java.util.UUID
import kotlin.math.sin

@Composable
fun GameOfLifeScreen(content: String) {
    val vmKey = remember { UUID.randomUUID().toString() }
    val viewModel: GameOfLifeViewModel =
        hiltViewModel<GameOfLifeViewModel, GameOfLifeViewModel.Factory>(key = vmKey) { it.create(content) }
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    BackHandler { viewModel.onViewEvent(OnBackPressed) }
    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            Back -> navigator.back()
        }
    }
    LifecycleStartEffect(Unit) {
        onStopOrDispose { viewModel.onViewEvent(OnStopPressed) }
    }

    val view = LocalView.current
    DisposableEffect(viewState.isPaused) {
        view.keepScreenOn = !viewState.isPaused
        onDispose { view.keepScreenOn = false }
    }

    Content(state = viewState, viewEvent = viewModel::onViewEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: GameOfLifeViewState,
    viewEvent: (GameOfLifeViewModel.ViewEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.game_of_life_title)) },
                navigationIcon = {
                    IconButton(onClick = { viewEvent(OnBackPressed) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    Text(
                        text = stringResource(R.string.game_of_life_generation, state.step),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    FilledTonalIconButton(onClick = {
                        viewEvent(if (state.isPaused) OnResumePressed else OnStopPressed)
                    }) {
                        val cd = stringResource(if (state.isPaused) R.string.game_of_life_resume else R.string.game_of_life_pause)
                        Icon(
                            imageVector = if (state.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = cd
                        )
                    }
                    FilledTonalIconButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { viewEvent(OnRestartPressed) }
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.game_of_life_restart))
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            val gridSize = min(maxWidth, maxHeight) - 24.dp
            if (state.data.isNotEmpty()) {
                GridAmbiLight(state = state, gridSize = gridSize)
                GridReflection(state = state, gridSize = gridSize)
                Grid(state = state, gridSize = gridSize)
            }
        }
    }
}


@Composable
private fun GridReflection(state: GameOfLifeViewState, gridSize: Dp) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1_000_000, easing = LinearEasing)),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSizePx = gridSize.toPx()
        val cellSize = gridSizePx / state.resolution
        val gridLeft = (size.width - gridSizePx) / 2f
        val gridTop = (size.height - gridSizePx) / 2f
        val gridBottom = gridTop + gridSizePx
        val maxDist = gridSizePx * 0.5f

        for (x in 0 until state.resolution) {
            for (y in 0 until state.resolution) {
                if (!state.data[x][y]) continue

                val origCenterY = gridTop + y * cellSize + cellSize / 2f
                val reflCenterY = 2f * gridBottom - origCenterY
                val dist = reflCenterY - gridBottom
                if (dist > maxDist) continue

                val t = dist / maxDist
                val alpha = (1f - t) * 0.4f
                val waveAmp = cellSize * 0.8f * t
                val waveX = sin(reflCenterY * 0.05 + time * 2.5).toFloat() * waveAmp
                val stretchW = cellSize * (1f + t * 6f)
                val cellLeft = gridLeft + x * cellSize + waveX - (stretchW - cellSize) / 2f

                drawRect(
                    color = primaryColor.copy(alpha = alpha),
                    topLeft = Offset(cellLeft, reflCenterY - cellSize / 2f),
                    size = Size(stretchW, cellSize * (1f - t * 0.5f).coerceAtLeast(0.3f))
                )
            }
        }
    }
}

@Composable
private fun GridAmbiLight(state: GameOfLifeViewState, gridSize: Dp) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                renderEffect = BlurEffect(150f, 150f, TileMode.Clamp)
            }
    ) {
        val gridSizePx = gridSize.toPx()
        val cellSize = gridSizePx / state.resolution
        val offsetX = (size.width - gridSizePx) / 2f
        val offsetY = (size.height - gridSizePx) / 2f

        repeat(6) {
            for (x in 0 until state.resolution) {
                for (y in 0 until state.resolution) {
                    if (state.data[x][y]) {
                        val cx = offsetX + x * cellSize + cellSize / 2f
                        val cy = offsetY + y * cellSize + cellSize / 2f
                        val inflated = cellSize * 3f
                        drawRect(
                            color = primaryColor,
                            topLeft = Offset(cx - inflated / 2f, cy - inflated / 2f),
                            size = Size(inflated, inflated)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Grid(state: GameOfLifeViewState, gridSize: Dp) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val gridLineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)

    val scale = remember { Animatable(1f) }
    LaunchedEffect(state.step) {
        scale.snapTo(1.018f)
        scale.animateTo(1f, animationSpec = tween(durationMillis = 350))
    }

    val scanlineShader = remember { RuntimeShader(SCANLINE_SHADER) }
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1_000_000, easing = LinearEasing)),
        label = "time"
    )

    Canvas(
        modifier = Modifier
            .size(gridSize)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
    ) {
        val canvasSize = size.width
        val space = canvasSize / state.resolution
        val roundedPath = Path().apply {
            addRoundRect(RoundRect(0f, 0f, canvasSize, canvasSize, CornerRadius(16.dp.toPx())))
        }

        clipPath(roundedPath) {
            drawRect(color = surfaceVariantColor, size = Size(canvasSize, canvasSize))

            for (i in 0..state.resolution) {
                val pos = i * space
                drawLine(gridLineColor, Offset(pos, 0f), Offset(pos, canvasSize), 0.5f)
                drawLine(gridLineColor, Offset(0f, pos), Offset(canvasSize, pos), 0.5f)
            }

            // Cell pass
            for (x in 0 until state.resolution) {
                for (y in 0 until state.resolution) {
                    if (state.data[x][y]) {
                        drawRect(
                            color = primaryColor,
                            topLeft = Offset(x * space + 0.5f, y * space + 0.5f),
                            size = Size(space - 1f, space - 1f)
                        )
                    }
                }
            }

            // Animated scanlines via shader overlay
            scanlineShader.setFloatUniform("time", time)
            drawRect(brush = ShaderBrush(scanlineShader), size = Size(canvasSize, canvasSize))

            // Vignette
            drawRect(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(0.5f to Color.Transparent, 1.0f to Color.Black.copy(alpha = 0.7f)),
                    center = Offset(canvasSize / 2f, canvasSize / 2f),
                    radius = canvasSize * 0.75f
                ),
                size = Size(canvasSize, canvasSize)
            )
        }
    }
}


private const val SCANLINE_SHADER = """
    uniform float time;

    half4 main(float2 fragCoord) {
        float scrolled = fragCoord.y + time * 80.0;
        float line = mod(floor(scrolled / 2.0), 2.0);
        half alpha = half(line * 0.30);
        return half4(0.0, 0.0, 0.0, alpha);
    }
"""
