package com.example.composecustomview.samples

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun CanvasTest() {
    var points by rememberSaveable {
        mutableStateOf<List<Point>>(listOf())
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(key1 = Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        points = points + Point(change.position, isStartedPosition = false)
                    },
                    onDragStart = {
                        points = points + Point(it, isStartedPosition = true)
                    }
                )
            }
    ) {
        val path = Path()

        points.forEach { point ->
            if (point.isStartedPosition) {
                path.moveTo(point.offset.x, point.offset.y)
            } else {
                path.lineTo(point.offset.x, point.offset.y)
            }
        }
        drawPath(
            path = path,
            brush = Brush.linearGradient(listOf(Color.Cyan, Color.Magenta, Color.Red)),
            style = Stroke(width = 10.dp.toPx())
        )
    }
}

data class Point(
    val offset: Offset,
    val isStartedPosition: Boolean
)

@Composable
fun Dp.toPx() = with(LocalDensity.current) {
    this@toPx.toPx()
}

@Composable
fun Oleg() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        drawCircle(
            color = Color.White,
            radius = 20.dp.toPx(),
            center = Offset(25.dp.toPx(), 25.dp.toPx()),
            style = Stroke(width = 1.dp.toPx()),
        )

        drawLine(
            color = Color.White,
            start = Offset(70.dp.toPx(), 5.dp.toPx()),
            end = Offset(55.dp.toPx(), 45.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White,
            start = Offset(70.dp.toPx(), 5.dp.toPx()),
            end = Offset(85.dp.toPx(), 45.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )

        drawLine(
            color = Color.White,
            start = Offset(95.dp.toPx(), 5.dp.toPx()),
            end = Offset(95.dp.toPx(), 45.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White,
            start = Offset(95.dp.toPx(), 5.dp.toPx()),
            end = Offset(120.dp.toPx(), 5.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White,
            start = Offset(95.dp.toPx(), 25.dp.toPx()),
            end = Offset(120.dp.toPx(), 25.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White,
            start = Offset(95.dp.toPx(), 45.dp.toPx()),
            end = Offset(120.dp.toPx(), 45.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )

        drawLine(
            color = Color.White,
            start = Offset(130.dp.toPx(), 5.dp.toPx()),
            end = Offset(130.dp.toPx(), 45.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White,
            start = Offset(130.dp.toPx(), 5.dp.toPx()),
            end = Offset(150.dp.toPx(), 5.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
    }
}

