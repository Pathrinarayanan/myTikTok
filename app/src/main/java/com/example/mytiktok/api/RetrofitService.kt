package com.example.mytiktok.api

import com.example.mytiktok.modal.VideoResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitService{
    @GET("v1/videos/popular")
    suspend fun popular(
        @Query("page") page : Int,
        @Query("per_page") perPage : Int = 10
    ) : VideoResponse
}

object RetrofitHelper{
    private val http = OkHttpClient.Builder()
        .build()

    val api : RetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            . build()
            .create(
                RetrofitService::class.java
            )
    }
}

