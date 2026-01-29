package com.sayler666.gina.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sayler666.domain.model.journal.Mood
import com.sayler666.domain.model.journal.MoodAverage
import com.sayler666.gina.mood.ui.awesomeColor
import com.sayler666.gina.mood.ui.badColor
import com.sayler666.gina.mood.ui.goodColor
import com.sayler666.gina.mood.ui.lowColor
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.mood.ui.neutralColor
import com.sayler666.gina.mood.ui.superbColor
import com.sayler666.gina.settings.Theme
import com.sayler666.gina.ui.theme.GinaTheme
import com.sayler666.gina.ui.theme.RobotoSlabRegular
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME


@Composable
fun MoodLineChart(
    moods: List<MoodAverage>,
    modifier: Modifier = Modifier,
    dateLabelFormatter: (LocalDate) -> String,
) {
    val scrollState = rememberScrollState()

    val yLegendIconSize = 16.dp

    val minPointWidth = 40.dp
    val paddingDp = 20.dp

    val chartWidth = maxOf(a = 300.dp, b = minPointWidth * moods.size)
    val legendColor = MaterialTheme.colorScheme.onSurfaceVariant

    val awesomeColor = awesomeColor()
    val badColor = badColor()
    val lowColor = lowColor()
    val neutralColor = neutralColor()
    val goodColor = goodColor()
    val superbColor = superbColor()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .horizontalScroll(scrollState, reverseScrolling = true)
        ) {
            val textMeasurer = rememberTextMeasurer()

            Canvas(
                modifier = Modifier
                    .width(chartWidth)
                    .fillMaxHeight()
                    .padding(start = 16.dp, bottom = paddingDp)
            ) {
                if (moods.isEmpty()) return@Canvas

                val reversedMoods = moods.reversed()

                val canvasWidth = size.width
                val canvasHeight = size.height
                val paddingPx = paddingDp.toPx()

                val chartWidth = canvasWidth - 2 * paddingPx
                val chartHeight = canvasHeight - 2 * paddingPx

                val minY = -2f
                val maxY = 3f
                val yRange = maxY - minY

                fun valueToY(value: Float): Float {
                    return paddingPx + chartHeight * (1 - (value - minY) / yRange)
                }

                fun indexToX(index: Int): Float = paddingPx + (index.toFloat() / maxOf(
                    reversedMoods.size - 1,
                    1
                )) * chartWidth


                // grid
                for (i in 0..5) {
                    val value = minY + (i * yRange / 5)
                    val y = valueToY(value)

                    drawLine(
                        color = Color.Gray.copy(alpha = 0.1f),
                        start = Offset(paddingPx, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // chart cubic
                if (reversedMoods.size > 1) {
                    val path = Path()

                    if (reversedMoods.size == 2) {
                        path.moveTo(indexToX(0), valueToY(reversedMoods[0].moodAvg))
                        path.lineTo(indexToX(1), valueToY(reversedMoods[1].moodAvg))
                    } else {
                        path.moveTo(indexToX(0), valueToY(reversedMoods[0].moodAvg))

                        for (i in 1 until reversedMoods.size) {
                            val currentX = indexToX(i)
                            val currentY = valueToY(reversedMoods[i].moodAvg)
                            val prevX = indexToX(i - 1)
                            val prevY = valueToY(reversedMoods[i - 1].moodAvg)

                            val controlX1 = prevX + (currentX - prevX) * 0.33f
                            val controlX2 = prevX + (currentX - prevX) * 0.67f

                            path.cubicTo(
                                x1 = controlX1,
                                y1 = prevY,
                                x2 = controlX2,
                                y2 = currentY,
                                x3 = currentX,
                                y3 = currentY
                            )
                        }
                    }

                    // gradient
                    val chartBrush = Brush.verticalGradient(
                        colors = listOf(
                            awesomeColor,
                            superbColor,
                            goodColor,
                            neutralColor,
                            lowColor,
                            badColor,
                        ),
                        startY = paddingPx,
                        endY = canvasHeight - paddingPx
                    )

                    drawPath(
                        path = path,
                        brush = chartBrush,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                fun getExactColorFromGradient(value: Float): Color {
                    val normalizedValue = (value - (-2f)) / (3f - (-2f))

                    val colors = listOf(
                        badColor,
                        lowColor,
                        neutralColor,
                        goodColor,
                        superbColor,
                        awesomeColor
                    )

                    val segmentSize = 1f / (colors.size - 1)
                    val segmentIndex =
                        (normalizedValue / segmentSize).coerceIn(0f, (colors.size - 2).toFloat())
                    val lowerIndex = segmentIndex.toInt()
                    val upperIndex = (lowerIndex + 1).coerceAtMost(colors.size - 1)

                    val segmentPosition = (segmentIndex - lowerIndex)

                    return lerp(colors[lowerIndex], colors[upperIndex], segmentPosition)
                }

                reversedMoods.forEachIndexed { index, mood ->
                    drawCircle(
                        color = getExactColorFromGradient(mood.moodAvg),
                        radius = 4.dp.toPx(),
                        center = Offset(indexToX(index), valueToY(mood.moodAvg))
                    )
                }

                // x labels
                reversedMoods.forEachIndexed { index, mood ->
                    val x = indexToX(index)
                    val periodLabel = mood.period

                    val monthTextResult = textMeasurer.measure(
                        text = AnnotatedString(dateLabelFormatter(periodLabel)),
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = legendColor,
                            fontFamily = RobotoSlabRegular
                        )
                    )

                    drawText(
                        textLayoutResult = monthTextResult,
                        topLeft = Offset(
                            x = x - monthTextResult.size.width / 2f,
                            y = canvasHeight - paddingPx + 12.dp.toPx()
                        )
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .width(width = yLegendIconSize)
                .fillMaxHeight()
        ) {
            val moodIcons = Mood.entries.map { it.mapToMoodIcon() }.takeLast(6)
            val moodIconsPainters = moodIcons.map { rememberVectorPainter(image = it.icon) }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = paddingDp)
            ) {
                val iconSizePxf = yLegendIconSize.toPx()
                val canvasHeight = size.height
                val padding = paddingDp.toPx()

                val chartHeight = canvasHeight - 2 * padding

                val minY = -2f
                val maxY = 3f
                val yRange = maxY - minY

                fun valueToY(value: Float): Float {
                    return padding + chartHeight * (1 - (value - minY) / yRange)
                }

                for (i in 0..5) {
                    val value = minY + (i * yRange / 5)
                    val y = valueToY(value)

                    with(moodIconsPainters[i]) {
                        withTransform({
                            translate(
                                top = y - iconSizePxf / 2,
                            )
                        }) {
                            draw(
                                size = Size(iconSizePxf, iconSizePxf),
                                colorFilter = ColorFilter.tint(color = moodIcons[i].color),
                            )
                        }
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LineChartPreview() {
    val sampleMoods = listOf(
        MoodAverage(LocalDate.of(2023, Month.JANUARY, 1), 2.2f),
        MoodAverage(LocalDate.of(2023, Month.FEBRUARY, 21), 1.2f),
        MoodAverage(LocalDate.of(2023, Month.MARCH, 2), -1.2f),
        MoodAverage(LocalDate.of(2023, Month.JUNE, 23), 2.2f),
        MoodAverage(LocalDate.of(2023, Month.AUGUST, 4), 0.3f),
        MoodAverage(LocalDate.of(2024, Month.JUNE, 1), -1.2f),

        )

    GinaTheme(
        theme = Theme.Firewatch,
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) {
            MoodLineChart(
                modifier = Modifier.height(250.dp),
                moods = sampleMoods,
                dateLabelFormatter = { date ->
                    date.format(DateTimeFormatter.ofPattern("d MMM\nyyyy"))
                }
            )
        }
    }
}
