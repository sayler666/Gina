package com.sayler666.gina.insights.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sayler666.gina.insights.viewmodel.MoodChartData
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.EmptyResult

@Composable
fun DoughnutChart(
    values: List<MoodChartData>,
    size: Dp = 60.dp,
    thickness: Dp = 20.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(bottom = 8.dp)) {
            Text(
                text = "Moods graph",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
            if (values.isNotEmpty()) {
                Chart(values, size, thickness)
            } else {
                EmptyResult(
                    "No data found!",
                    "No moods found within given filters.",
                    headerStyle = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun Chart(
    values: List<MoodChartData>,
    size: Dp,
    thickness: Dp
) {
    val sumOfValues = values.map { it.value }.sum()
    val proportions = values.map { it.value }.map {
        it * 100 / sumOfValues
    }
    val sweepAngles = proportions.map {
        360 * it / 100
    }
    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 22.dp, bottom = 16.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(size)
                .height(size + 10.dp)
                .padding(top = 5.dp)
        ) {
            val colors = mutableListOf<Color>()
            for (i in values.indices) {
                colors.add(i, values[i].mood.mapToMoodIcon().color)
            }
            Canvas(
                modifier = Modifier.size(size = size)
            ) {
                var startAngle = -90f
                for (i in values.indices) {
                    drawArc(
                        color = colors[i],
                        startAngle = startAngle,
                        sweepAngle = sweepAngles[i],
                        useCenter = false,
                        style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngles[i]
                }
            }
        }
        Legend(values)
    }
}

@Composable
private fun Legend(values: List<MoodChartData>) {
    val sumOfValues = values.map { it.value }.sum()
    val proportions = values.map { it.value }.map {
        it * 100 / sumOfValues
    }
    Column(
        Modifier
            .padding(start = 28.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        values.forEachIndexed { i, mood ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(color = mood.mood.mapToMoodIcon().color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = mood.mood.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            + " %.2f".format(proportions[i]) + "%" + " (${mood.value.toInt()})",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
