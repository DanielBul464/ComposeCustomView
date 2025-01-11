package com.example.composecustomview.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composecustomview.data.ApiFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class MainViewModel : ViewModel() {

    private val apiService = ApiFactory.apiService

    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Initial)
    val state = _state.asStateFlow()

    private var lastState: MainScreenState = MainScreenState.Initial

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("MainViewModel", "Exception caught: $throwable")
        _state.value = lastState
    }

    init {
        loadBarList()
    }

    fun loadBarList(timeFrame: TimeFrame = TimeFrame.HOUR_1) {
        lastState = _state.value
        _state.value = MainScreenState.Loading
        viewModelScope.launch(context = exceptionHandler + Dispatchers.IO) {
            val barList = apiService.loadBars(timeFrame.apiRequestTimeValue).barList
            _state.value = MainScreenState.Content(barList, timeFrame)
        }
    }

}