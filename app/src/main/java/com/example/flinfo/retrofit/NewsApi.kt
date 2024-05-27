package com.example.flinfo.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApi {

    @GET("/trending")
    fun getNews(@Query("pageNumber") pageNumber: Int, @Query("recordCount") recordCount: Int, @Query("language") language: String = "Mandarin"): Call<TrendingNewsResponse>

    @GET("fl-learn/{language}/{hskLevel}")
    fun getHskNews(
        @Path("language") language: String,
        @Path("hskLevel") hskLevel: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("recordCount") recordCount: Int
    ): Call<TrendingNewsResponse>

    @GET("doc/{uuid}")
    fun getArticle(@Path("uuid") uuid: String): Call<ArticleResponse>

    @GET("fl-learn/{uuid}")
    fun getArticleInLearningMode(@Path("uuid") uuid: String): Call<LearningModeResponse>
}
