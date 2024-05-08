package com.example.newsapp.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("/trending")
    fun getNews(@Query("pageNumber") pageNumber: Int, @Query("recordCount") recordCount: Int): Call<TrendingNewsResponse>

}