package com.example.composecustomview.presentation

import com.example.composecustomview.data.Bar

sealed class MainScreenState {

    data object Initial : MainScreenState()
    data object Loading : MainScreenState()
    data class Content(val barList: List<Bar>, val timeFrame: TimeFrame) : MainScreenState()

}