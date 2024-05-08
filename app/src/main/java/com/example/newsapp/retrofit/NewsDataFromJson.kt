package com.example.newsapp.retrofit

import com.example.newsapp.NewsModel

data class TrendingNewsResponse(
    val header: Header,
    val results: List<NewsArticle>
)

data class Header(
    val hits: Int,
    val hitsTotal: Int
)

data class NewsArticle(
    val uuid: String,
    val sourceArticleLanguage: String,
    val sourceArticleUrl: String,
    val sourceArticleTitles: List<String>,
    val sourceArticleText: String,
    val articleDate: List<Int>,
    val documentThumbnail: String,
    val defaultDocumentThumbnail: String,
    val firstCrawledAt: Double,
    val lastModifiedAt: Double,
    val lastModifiedAtAsText: String,
    val websiteUrl: String,
    val websiteTitle: String,
    val websiteSubject: String,
    val websiteDescription: String,
    val topNews: Boolean,
    val premium: Boolean,
    val trendingInWWR: Boolean,
    val archived: Boolean,
    val textLength: Int,
    val timeToRead: String,
    val translatedSummary: String,
    val translationLanguage: String,
    val translatedTrimmedText: String,
    val translatedText: String,
    val author: String,
    val translatedTitle: String,
    val transliteration: String,
    val kbTopics: List<String>,
    val kbKeywords: List<String>,
    val kbRelatedKeywords: List<String>,
    val languageDifficultyScores: Map<String, String>,
    val languageDifficultyLevel: String,
    val translatedTitleSentimentScores: Map<String, String>
){
    fun toNewsModel(): NewsModel {
        return NewsModel(
            uuid = this.uuid,
            headLine = this.translatedTitle,
            image = this.documentThumbnail,
            description = this.translatedTrimmedText,
            url = this.sourceArticleUrl,
            source = this.websiteTitle,
            time = this.lastModifiedAtAsText,
            content = this.translatedSummary
        )
    }
}
