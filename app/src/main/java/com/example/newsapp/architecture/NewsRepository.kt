package com.example.newsapp.architecture

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.MainActivity
import com.example.newsapp.NewsModel
import com.example.newsapp.retrofit.NewsApi
import com.example.newsapp.retrofit.NewsArticle
import com.example.newsapp.retrofit.RetrofitHelper
import com.example.newsapp.retrofit.TrendingNewsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    // get news from API
    fun getNewsApiCall(pageNumber: Int, recordCount: Int): MutableLiveData<List<NewsArticle>> {

        val newsList = MutableLiveData<List<NewsArticle>>()

        val call = RetrofitHelper.getInstance().create(NewsApi::class.java)
            .getNews(pageNumber, recordCount)

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
                            MainActivity.apiRequestError = true
                            MainActivity.errorMessage = jsonObj.getString("message")
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

                MainActivity.apiRequestError = true
                MainActivity.errorMessage = t.localizedMessage as String
                Log.d("err_msg", "msg" + t.localizedMessage)
            }
        })

        return newsList
    }
}

