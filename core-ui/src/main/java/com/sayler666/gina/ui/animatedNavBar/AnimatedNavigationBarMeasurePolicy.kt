package com.sayler666.gina.ui.animatedNavBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.IntOffset


@Composable
fun animatedNavBarMeasurePolicy(
    menuItemsSize: Int,
    onBallPositionsCalculated: (ArrayList<Float>) -> Unit
) = remember {
    barMeasurePolicy(
        onBallPositionsCalculated = onBallPositionsCalculated,
        menuItemsSize = menuItemsSize
    )
}

internal fun barMeasurePolicy(
    menuItemsSize: Int,
    onBallPositionsCalculated: (ArrayList<Float>) -> Unit
) = MeasurePolicy { measurables, constraints ->

    val itemWidth = constraints.maxWidth / menuItemsSize
    val placeables = measurables.map { measurable ->
        measurable.measure(constraints.copy(maxWidth = itemWidth))
    }

    val gap = calculateGap(placeables, constraints.maxWidth, menuItemsSize)
    val height = placeables.maxOf { it.height }

    layout(constraints.maxWidth, height) {
        var xPosition = gap
        val buttonsPositions = arrayListOf<Float>()

        placeables.take(menuItemsSize).forEach { buttons ->
            buttons.placeRelative(xPosition, 0)
            buttonsPositions.add(element = calculatePointPosition(xPosition, buttons.width))
            xPosition += buttons.width + gap
        }

        if (placeables.size > menuItemsSize)
            placeables.last().let { activeIndicator ->
                activeIndicator.place(
                    IntOffset(0, (constraints.maxHeight - activeIndicator.height) / 2)
                )
            }

        onBallPositionsCalculated(buttonsPositions)
    }
}

private fun calculatePointPosition(xButtonPosition: Int, buttonWidth: Int): Float =
    xButtonPosition + (buttonWidth / 2f)

private fun calculateGap(placeables: List<Placeable>, width: Int, menuItemsSize: Int): Int {
    val allWidth = placeables.take(menuItemsSize).sumOf { it.width }
    return (width - allWidth) / (placeables.size)
}
