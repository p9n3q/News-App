package com.example.flinfo.architecture

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flinfo.NewsModel

class NewsViewModel : ViewModel() {

    private var newsLiveData: MutableLiveData<List<NewsModel>>? = null

    //get news from API
    fun getNews(): MutableLiveData<List<NewsModel>>? {
        val newsLiveData = NewsRepository().getNewsApiCall(1, 20)

        val mappedNewsLiveData = MediatorLiveData<List<NewsModel>>().apply {
            addSource(newsLiveData) { newsArticles ->
                value = newsArticles?.map { newsArticle ->
                    newsArticle.toNewsModel()
                }
            }
        }
        return mappedNewsLiveData
    }

    var newsData: LiveData<List<NewsModel>>? = null

    fun insertNews(context: Context, news: NewsModel) {
        NewsRepository.insertNews(context, news)
    }

    fun deleteNews(context: Context, news: NewsModel) {
        NewsRepository.deleteNews(context, news)
    }

    fun getNewsFromDB(context: Context): LiveData<List<NewsModel>>? {
        newsData = NewsRepository.getAllNews(context)
        return newsData
    }

    fun getArticle(uuid: String): LiveData<NewsModel> {
        return NewsRepository().getArticleApiCall(uuid)
    }
}
