package com.example.flinfo.architecture

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flinfo.LoginActivity
import com.example.flinfo.MainActivity
import com.example.flinfo.MyApp
import com.example.flinfo.NewsModel
import com.example.flinfo.retrofit.ArticleResponse
import com.example.flinfo.retrofit.LearningModeResponse
import com.example.flinfo.retrofit.NewsApi
import com.example.flinfo.retrofit.NewsArticle
import com.example.flinfo.retrofit.RetrofitHelper
import com.example.flinfo.retrofit.TrendingNewsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NewsRepository {

    companion object {

        private var newsDatabase: NewsDatabase? = null

        private fun initializeDB(context: Context): NewsDatabase {
            return NewsDatabase.getDatabaseClient(context)
        }

        fun insertNews(context: Context, news: NewsModel) {

            newsDatabase = initializeDB(context)
            CoroutineScope(IO).launch {
                newsDatabase!!.newsDao().insertNews(news)
            }
        }

        fun deleteNews(context: Context, news: NewsModel) {

            newsDatabase = initializeDB(context)
            CoroutineScope(IO).launch {
                newsDatabase!!.newsDao().deleteNews(news)
            }
        }

        fun getAllNews(context: Context): LiveData<List<NewsModel>> {

            newsDatabase = initializeDB(context)
            return newsDatabase!!.newsDao().getNewsFromDatabase()
        }

    }

    fun getNewsApiCall(pageNumber: Int, recordCount: Int): MutableLiveData<List<NewsArticle>> {

        val newsList = MutableLiveData<List<NewsArticle>>()

        val apiService = RetrofitHelper.getNewsApiService()
        val call = apiService.getNews(pageNumber, recordCount)

        call.enqueue(object : Callback<TrendingNewsResponse> {
            override fun onResponse(
                call: Call<TrendingNewsResponse>,
                response: Response<TrendingNewsResponse>
            ) {

                if (response.isSuccessful) {

                    val body = response.body()
                    Log.d("API_RESPONSE", "Success: ${body}")

                    if (body != null) {
                        newsList.value = body.results
                        Log.d("API_RESPONSE", "Success: ${newsList.value}")
                    }

                } else {

                    val jsonObj: JSONObject?

                    try {
                        jsonObj = response.errorBody()?.string()?.let { JSONObject(it) }
                        if (jsonObj != null) {
                            handleAuthorizationError(response, jsonObj.getString("message"))
                            val tempNewsList = mutableListOf<NewsArticle>()
                            newsList.value = tempNewsList
                            Log.d("API_RESPONSE", "Error: ${jsonObj.getString("message")}")
                        }
                    } catch (e: JSONException) {
                        Log.d("JSONException", "" + e.message)
                    }

                }
            }

            override fun onFailure(call: Call<TrendingNewsResponse>, t: Throwable) {
                handleAuthorizationError(null, t.localizedMessage ?: "Unknown error")
                Log.d("err_msg", "msg" + t.localizedMessage)
            }
        })

        return newsList
    }


    fun getHskNewsApiCall(hskLevel: String, language: String, pageNumber: Int, recordCount: Int): MutableLiveData<List<NewsArticle>> {

        val newsList = MutableLiveData<List<NewsArticle>>()

        val apiService = RetrofitHelper.getNewsApiService()
        val call = apiService.getHskNews(hskLevel, language, pageNumber, recordCount)

        call.enqueue(object : Callback<TrendingNewsResponse> {
            override fun onResponse(
                call: Call<TrendingNewsResponse>,
                response: Response<TrendingNewsResponse>
            ) {

                if (response.isSuccessful) {

                    val body = response.body()
                    Log.d("API_RESPONSE", "Success: ${body}")

                    if (body != null) {
                        newsList.value = body.results
                        Log.d("API_RESPONSE", "Success: ${newsList.value}")
                    }

                } else {

                    val jsonObj: JSONObject?

                    try {
                        jsonObj = response.errorBody()?.string()?.let { JSONObject(it) }
                        if (jsonObj != null) {
                            handleAuthorizationError(response, jsonObj.getString("message"))
                            val tempNewsList = mutableListOf<NewsArticle>()
                            newsList.value = tempNewsList
                            Log.d("API_RESPONSE", "Error: ${jsonObj.getString("message")}")
                        }
                    } catch (e: JSONException) {
                        Log.d("JSONException", "" + e.message)
                    }

                }
            }

            override fun onFailure(call: Call<TrendingNewsResponse>, t: Throwable) {
                handleAuthorizationError(null, t.localizedMessage ?: "Unknown error")
                Log.d("err_msg", "msg" + t.localizedMessage)
            }
        })

        return newsList
    }

    fun getArticleApiCall(uuid: String): LiveData<NewsModel> {
        val articleLiveData = MutableLiveData<NewsModel>()

        val apiService = RetrofitHelper.getNewsApiService()
        val call = apiService.getArticle(uuid)

        call.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val article = body.toNewsModel()
                        articleLiveData.value = article
                    }
                } else {
                    val jsonObj: JSONObject?
                    try {
                        jsonObj = response.errorBody()?.string()?.let { JSONObject(it) }
                        if (jsonObj != null) {
                            handleAuthorizationError(response, jsonObj.getString("message"))
                        }
                    } catch (e: JSONException) {
                        Log.d("JSONException", "" + e.message)
                    }
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                handleAuthorizationError(null,t.localizedMessage ?: "Unknown error")
                Log.d("err_msg", "msg" + t.localizedMessage)
            }
        })

        return articleLiveData
    }

    fun getArticleInLearningModeApiCall(uuid: String): LiveData<LearningModeResponse> {
        val liveData = MutableLiveData<LearningModeResponse>()
        val apiService = RetrofitHelper.getNewsApiService()
        val call = apiService.getArticleInLearningMode(uuid)

        call.enqueue(object : Callback<LearningModeResponse> {
            override fun onResponse(call: Call<LearningModeResponse>, response: Response<LearningModeResponse>) {
                if (response.isSuccessful) {
                    liveData.value = response.body()
                }
            }

            override fun onFailure(call: Call<LearningModeResponse>, t: Throwable) {
                handleAuthorizationError(null,t.localizedMessage ?: "Unknown error")
                Log.d("err_msg", "msg" + t.localizedMessage)            }
        })

        return liveData
    }

    private fun handleAuthorizationError(response: Response<*>?, errorMessage: String) {
        if (response != null && (response.code() == 401 || response.code() == 403) || errorMessage == "Unauthorized" || errorMessage == "Authorization failed" || errorMessage == "Invalid Token") {
            // Redirect to login activity
            val intent = Intent(MyApp.instance, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            MyApp.instance.startActivity(intent)
        } else {
            MainActivity.apiRequestError = true
            MainActivity.errorMessage = errorMessage
        }
    }

}

