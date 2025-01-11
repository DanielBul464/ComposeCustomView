package com.example.composecustomview.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecustomview.data.Bar
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS_COUNT = 20

@Composable
fun Graph(
    modifier: Modifier = Modifier,
) {

    val viewModel: MainViewModel = viewModel()
    val screenState by viewModel.state.collectAsState()
    when (val currentState = screenState) {
        is MainScreenState.Content -> {
            val graphState = rememberGraphState(currentState.barList)

            Chart(
                modifier = modifier,
                graphState = graphState,
                timeFrame = currentState.timeFrame
            ) {
                graphState.value = it
            }

            currentState.barList.firstOrNull()?.let {
                Prices(
                    modifier = modifier,
                    graphState = graphState,
                    lastPrice = it.close
                )
            }

            TimeFrames(currentState.timeFrame, viewModel::loadBarList)
        }

        is MainScreenState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is MainScreenState.Initial -> {}
    }
}

@Composable
private fun TimeFrames(
    selectedFrame: TimeFrame,
    onTimeFrameSelected: (TimeFrame) -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeFrame.entries.forEach { timeFrame ->
            val isSelected = selectedFrame == timeFrame
            AssistChip(
                onClick = { onTimeFrameSelected.invoke(timeFrame) },
                label = { Text(timeFrame.label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) Color.White else Color.Black,
                    labelColor = if (isSelected) Color.Black else Color.White,
                )
            )
        }
    }
}

@Composable
private fun Chart(
    modifier: Modifier = Modifier,
    graphState: State<GraphState>,
    timeFrame: TimeFrame,
    onGraphStateChanged: (GraphState) -> Unit,
) {
    val currentState = graphState.value
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val visibleBarsCount = (currentState.visibleBarsCount / zoomChange)
            .roundToInt()
            .coerceIn(MIN_VISIBLE_BARS_COUNT, currentState.barList.size)
        val scrolledBy = (currentState.scrolledBy + panChange.x)
            .coerceAtLeast(0f)
            .coerceAtMost(currentState.barList.size * currentState.barWidth - currentState.graphWidth)
        onGraphStateChanged.invoke(
            currentState.copy(
                visibleBarsCount = visibleBarsCount,
                scrolledBy = scrolledBy
            )
        )
    }

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
            .padding(top = 32.dp, bottom = 32.dp, end = 32.dp)
            .transformable(transformableState)
            .onSizeChanged {
                onGraphStateChanged.invoke(
                    currentState.copy(
                        graphWidth = it.width.toFloat(),
                        graphHeight = it.height.toFloat()
                    )
                )
            }
    ) {
        with(currentState) {
            translate(left = scrolledBy) {
                barList.forEachIndexed { index, bar ->
                    val offsetX = size.width - index * barWidth
                    drawTimeDelimiter(
                        bar = bar,
                        nextBar = if (index < currentState.barList.size - 1) {
                            currentState.barList[index + 1]
                        } else {
                            null
                        },
                        timeFrame = timeFrame,
                        offsetX = offsetX,
                        textMeasurer = textMeasurer
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(offsetX, size.height - ((bar.low - min) * pxPerPoint)),
                        end = Offset(offsetX, size.height - ((bar.high - min) * pxPerPoint)),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = if (bar.close > bar.open) {
                            Color.Green
                        } else {
                            Color.Red
                        },
                        start = Offset(offsetX, size.height - ((bar.open - min) * pxPerPoint)),
                        end = Offset(offsetX, size.height - ((bar.close - min) * pxPerPoint)),
                        strokeWidth = barWidth / 2
                    )
                }
            }
        }

    }
}

@Composable
private fun Prices(
    modifier: Modifier = Modifier,
    graphState: State<GraphState>,
    lastPrice: Float,
) {
    val currentState = graphState.value
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(32.dp)
    ) {
        drawPrices(
            max = currentState.max,
            min = currentState.min,
            pxPerPoint = currentState.pxPerPoint,
            lastPrice = lastPrice,
            textMeasurer = textMeasurer
        )
    }
}

@SuppressLint("DefaultLocale")
private fun DrawScope.drawTimeDelimiter(
    bar: Bar,
    nextBar: Bar?,
    timeFrame: TimeFrame,
    offsetX: Float,
    textMeasurer: TextMeasurer
) {
    val calendar = bar.calendar

    val minutes = calendar.get(Calendar.MINUTE)
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val shouldDrawDelimiter = when (timeFrame) {
        TimeFrame.MIN_5 -> {
            minutes == 0
        }

        TimeFrame.MIN_15 -> {
            minutes == 0 && hours % 2 == 0
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            val nextBarDay = nextBar?.calendar?.get(Calendar.DAY_OF_MONTH)
            day != nextBarDay
        }
    }

    if (!shouldDrawDelimiter) return

    drawLine(
        color = Color.White.copy(0.5f),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX, size.height),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx())
        )
    )

    val nameOfMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val text = when (timeFrame) {
        TimeFrame.MIN_5, TimeFrame.MIN_15 -> {
            String.format("%02d:00", hours)
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            String.format("%s %s", day, nameOfMonth)
        }
    }
    val textLayoutResult = textMeasurer.measure(
        text = text,
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(offsetX - textLayoutResult.size.width / 2, size.height)
    )
}

private fun DrawScope.drawPrices(
    max: Float,
    min: Float,
    pxPerPoint: Float,
    lastPrice: Float,
    textMeasurer: TextMeasurer
) {
    //max
    val maxPriceOffsetY = 0f
    drawDashedLine(
        start = Offset(0f, maxPriceOffsetY),
        end = Offset(size.width, maxPriceOffsetY)
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = max,
        offsetY = maxPriceOffsetY
    )

    //last price
    val lastPriceOffsetY = size.height - ((lastPrice - min) * pxPerPoint)
    drawDashedLine(
        start = Offset(0f, lastPriceOffsetY),
        end = Offset(size.width, lastPriceOffsetY)
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = lastPrice,
        offsetY = lastPriceOffsetY
    )

    //min
    val minPriceOffsetY = size.height
    drawDashedLine(
        start = Offset(0f, minPriceOffsetY),
        end = Offset(size.width, minPriceOffsetY)
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = min,
        offsetY = minPriceOffsetY
    )
}

private fun DrawScope.drawTextPrice(
    textMeasurer: TextMeasurer,
    price: Float,
    offsetY: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width - 4.dp.toPx(), offsetY)
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 1f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )
}