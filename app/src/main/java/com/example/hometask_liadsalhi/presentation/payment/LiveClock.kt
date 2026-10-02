package com.example.hometask_liadsalhi.presentation.payment

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

// created once and reused every second
private val TimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

// analog clock face + digital time under it. It only draws the time it receives.
@Composable
fun LiveClock(time: LocalTime, modifier: Modifier = Modifier) {
    val faceColor = MaterialTheme.colorScheme.onSurface
    val secondHandColor = MaterialTheme.colorScheme.primary
    val timeText = time.format(TimeFormatter)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        // screen readers hear one clear sentence instead of a drawing
        modifier = modifier.clearAndSetSemantics { contentDescription = "Current time $timeText" }
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val radius = size.minDimension / 2

            // clock face: outer circle + 12 hour marks
            drawCircle(color = faceColor, radius = radius - 2.dp.toPx(), style = Stroke(width = 3.dp.toPx()))
            for (hour in 0 until 12) {
                drawLine(
                    color = faceColor,
                    start = pointAt(hour * 30f, radius * 0.80f),
                    end = pointAt(hour * 30f, radius * 0.92f),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // hand angles in degrees, 0 = 12 o'clock
            val hourAngle = (time.hour % 12) * 30f + time.minute * 0.5f
            val minuteAngle = time.minute * 6f + time.second * 0.1f
            val secondAngle = time.second * 6f

            // hands: hour (short, thick), minute (longer), second (longest, thin, colored)
            drawLine(faceColor, center, pointAt(hourAngle, radius * 0.50f), 6.dp.toPx(), StrokeCap.Round)
            drawLine(faceColor, center, pointAt(minuteAngle, radius * 0.72f), 4.dp.toPx(), StrokeCap.Round)
            drawLine(secondHandColor, center, pointAt(secondAngle, radius * 0.85f), 2.dp.toPx(), StrokeCap.Round)
            drawCircle(color = faceColor, radius = 4.dp.toPx())
        }

        Spacer(Modifier.height(8.dp))
        Text(text = timeText, style = MaterialTheme.typography.titleMedium)
    }
}

// the point at a given angle and distance from the center
private fun DrawScope.pointAt(angleDegrees: Float, distance: Float): Offset {
    val radians = Math.toRadians(angleDegrees - 90.0)
    return Offset(
        x = center.x + distance * cos(radians).toFloat(),
        y = center.y + distance * sin(radians).toFloat()
    )
}