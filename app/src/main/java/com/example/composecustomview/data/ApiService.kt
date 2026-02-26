package com.example.composecustomview.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("aggs/ticker/AAPL/range/{timeframe}/2025-01-01/2026-02-26?adjusted=true&sort=desc&apiKey=QCybc7N0P7klIrejd8FRgZflpIqJJqPf")
    suspend fun loadBars(
        @Path ("timeframe") timeframe: String
    ): Result

}