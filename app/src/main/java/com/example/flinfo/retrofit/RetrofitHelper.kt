package com.example.flinfo.retrofit

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private const val USERS_BASE_URL = "https://users.flinput.com/"
    private const val API_BASE_URL = "https://api.flinput.com/"

    private const val PREF_NAME = "FlinfoPrefs"
    private const val PREF_KEY_AUTH_TOKEN = "authorizationToken"

    private lateinit var authorizationToken: String
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        authorizationToken = sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, "") ?: ""
    }

    fun getUserApiService(): UserApi {
        return getInstance(USERS_BASE_URL).create(UserApi::class.java)
    }

    fun getNewsApiService(): NewsApi {
        return getInstance(API_BASE_URL).create(NewsApi::class.java)
    }

    fun getInstance(baseUrl: String): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                    authorizationToken?.let {
                        requestBuilder.header("Authorization", "Bearer $it")
                    }
                    requestBuilder.header("fullOutput", "true")
                    requestBuilder.header("esQuery", "true")
                    val request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Save the AuthorizationToken in SharedPreferences or any other persistence mechanism
    fun setAuthorizationToken(token: String) {
        authorizationToken = token
        sharedPreferences.edit().putString(PREF_KEY_AUTH_TOKEN, token).apply()
    }

    // Retrieve the AuthorizationToken from SharedPreferences or any other persistence mechanism
    fun getAuthorizationToken(): String? {
        // Retrieve the token from SharedPreferences or any other persistence mechanism
        return authorizationToken
    }
}
