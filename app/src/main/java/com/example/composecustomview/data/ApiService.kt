package com.example.composecustomview.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("aggs/ticker/AAPL/range/{timeframe}/2023-01-09/2023-02-10?adjusted=true&sort=desc&apiKey=QCybc7N0P7klIrejd8FRgZflpIqJJqPf")
    suspend fun loadBars(
        @Path ("timeframe") timeframe: String
    ): Result

}