package com.example.flinfo.retrofit

import android.os.Parcel
import android.os.Parcelable
import com.example.flinfo.NewsModel

data class AuthenticationResponse(
    val message: String,
    val stage: String,
    val flinfoToken: String,
    val daysLeftToExpiry: Int
)

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

data class ArticleResponse(
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
            content = this.translatedText
        )
    }
}

data class LearningModeResponse(
    val uuid: String,
    val originalTitle: String,
    val originalSourceArticleText: String,
    val title: List<Title>? = null,
    val createdAt: String,
    val lastModifiedAt: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Title)!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeString(originalTitle)
        parcel.writeString(originalSourceArticleText)
        parcel.writeTypedList(title)
        parcel.writeString(createdAt)
        parcel.writeString(lastModifiedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LearningModeResponse> {
        override fun createFromParcel(parcel: Parcel): LearningModeResponse {
            return LearningModeResponse(parcel)
        }

        override fun newArray(size: Int): Array<LearningModeResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class Title(
    val word: String?,
    val nature: String?,
    val offset: Int?,
    val frequency: Int?,
    val length: Int?,
    val pinyin: String?,
    val meaning: List<String>?,
    val source: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(word)
        parcel.writeString(nature)
        parcel.writeValue(offset)
        parcel.writeValue(frequency)
        parcel.writeValue(length)
        parcel.writeString(pinyin)
        parcel.writeStringList(meaning)
        parcel.writeString(source)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Title> {
        override fun createFromParcel(parcel: Parcel): Title {
            return Title(parcel)
        }

        override fun newArray(size: Int): Array<Title?> {
            return arrayOfNulls(size)
        }
    }
}