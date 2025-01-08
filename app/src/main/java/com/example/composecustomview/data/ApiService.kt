package com.example.composecustomview.data

import retrofit2.http.GET

interface ApiService {

    @GET("aggs/ticker/AAPL/range/1/hour/2023-01-09/2023-02-10?adjusted=true&sort=asc&apiKey=QCybc7N0P7klIrejd8FRgZflpIqJJqPf")
    suspend fun loadBars(): Result

}