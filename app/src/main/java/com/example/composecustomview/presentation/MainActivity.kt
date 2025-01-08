package com.example.composecustomview.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecustomview.ui.theme.ComposeCustomViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCustomViewTheme {
                val viewModel: MainViewModel = viewModel()
                val screenState by viewModel.state.collectAsState()
                when (val currentState = screenState) {
                    is MainScreenState.Content -> {
                        Log.d("MainActivity", currentState.barList.toString())
                    }

                    is MainScreenState.Initial -> {}
                }
            }
        }
    }
}