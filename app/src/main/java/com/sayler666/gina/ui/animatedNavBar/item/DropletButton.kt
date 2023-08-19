package com.sayler666.gina.ui.animatedNavBar.item

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sayler666.gina.ui.animatedNavBar.utils.noRippleClickable
import com.sayler666.gina.ui.toPxf

/**
 *
 *A composable button that displays an icon with a droplet-shaped background. The button supports animation
 *and selection states.
 *@param modifier Modifier to be applied to the composable
 *@param isSelected Boolean representing whether the button is currently selected or not
 *@param onClick Callback to be executed when the button is clicked
 *@param icon Drawable resource of the icon to be displayed on the button
 *@param contentDescription A description for the button to be used by accessibility
 *@param iconColor Color to tint the icon
 *@param dropletColor Color of the droplet-shaped background
 *@param size Icon size
 *@param animationSpec Animation specification to be used when animating the button
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DropletButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    dropletColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    size: Dp = 22.dp,
    animationSpec: AnimationSpec<Float> = remember { tween(400) }
) {
    Box(
        modifier = modifier.noRippleClickable { onClick() }
    ) {
        val density = LocalDensity.current
        val dropletButtonParams = animateDropletButtonAsState(
            isSelected = isSelected, animationSpec = animationSpec, size = size.toPxf(density)
        )

        val sizePx = remember(size) { size.toPxf(density) }
        val circleCenter by remember {
            derivedStateOf {
                mutableStateOf(sizePx / 2)
            }
        }

        val painter = rememberVectorPainter(image = icon)
        Canvas(
            modifier = Modifier
                .size(size)
                .align(Alignment.Center)
                .graphicsLayer(
                    alpha = 0.99f,
                    scaleX = dropletButtonParams.value.scale,
                    scaleY = dropletButtonParams.value.scale,
                ),
            contentDescription = contentDescription ?: ""
        ) {
            with(painter) {
                draw(
                    size = Size(sizePx, sizePx),
                    colorFilter = ColorFilter.tint(color = iconColor)
                )
            }

            drawCircle(
                color = dropletColor,
                radius = dropletButtonParams.value.radius,
                center = Offset(
                    circleCenter.value,
                    circleCenter.value
                ),
                blendMode = BlendMode.SrcIn
            )
        }
    }
}
