package com.example.composecustomview.presentation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.composecustomview.data.Bar
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Parcelize
data class GraphState(
    val barList: List<Bar>,
    val visibleBarsCount: Int = 100,
    val graphWidth: Float = 1f,
    val graphHeight: Float = 1f,
    val scrolledBy: Float = 0f
) : Parcelable {

    val barWidth: Float
        get() = graphWidth / visibleBarsCount

    private val visibleBars: List<Bar>
        get() {
            val startIndex = (scrolledBy / barWidth).roundToInt().coerceAtLeast(0)
            val endIndex = (startIndex + visibleBarsCount).coerceAtMost(barList.size)
            return barList.subList(startIndex, endIndex)
        }

    val max get() = visibleBars.maxOf { it.high }
    val min get() = visibleBars.minOf { it.high }
    val pxPerPoint get() = graphHeight / (max - min)

}

@Composable
fun rememberGraphState(bars: List<Bar>): MutableState<GraphState> {
    return rememberSaveable {
        mutableStateOf(GraphState(bars))
    }
}