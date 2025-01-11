package com.example.composecustomview.presentation

import com.example.composecustomview.R

enum class TimeFrame(val label: String, val apiRequestTimeValue: String) {
    MIN_5("M5", "5/minute"),
    MIN_15("M15", "15/minute"),
    MIN_30("M30", "30/minute"),
    HOUR_1("H1", "1/hour")
}