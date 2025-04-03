package com.example.watchview

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface StreamingApiService {

    @GET("search/title")
    fun searchTitle(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Query("title") title: String,
        @Query("country") country: String = "es"
    ): Call<ApiResponse>
}