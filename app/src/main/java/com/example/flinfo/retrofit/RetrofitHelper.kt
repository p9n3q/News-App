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

    private const val USERS_BASE_URL = "https://users.flinfo.com/"
    private const val API_BASE_URL = "https://api.flinfo.com/"

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
        //authorizationToken = "BqlfnnHB7bVJZl2Vw4owClSdjRAtX7V8O60TAorcDlczCpTIdjgTiIkjHvmtF5gpI8bJrXz3b5epcJvu1OBzNEysLmGd68wv/G7t8Rltv1G49rqwL264V1qvN6XY4SmnLa3mu9qB8TfdT0R6aaAODpBoiyiOwGh+Ofz3MCJypV62j6nwXYejyfJ2EyEZ82/J9k+9dUauaQICoMyoO4mOaz3ywvnogzW8SZCGGuYH8V0oNVyP3WcRXztQv6Z9ltz4PYJOpjzvd17IDIUQNcSWBQC20Sul5rfufJRypjHejJePEiNbAedBJwzXKCG0LLvK8aZawWykoYfldee1K+i/MToIPWiDjxepgaUvs7cMIB2QMLpN5pumwPNRm5M/aD4mv++XSzFqxdl6Ey+L7LYGg/w0whZpzTr+f8Vdk7nqfXkJgD+XacOr87Qzogr2Zmo7Wf+A5eY5HeP3Uem1dXuLMkN4W1fJBtrqmm5m7Rml0LIYNUsUG/5aywIltazpvOR2SVou4vfU2+EiQNc8mO/KxbJCX1gIOcWZdLxJNaWUqN7b08OWuHtwJfFllyrdGxHH9oaMDR89ySyUxBu/hjfuA6lVslBRlek/FdT/aE9C66tDK0FPXkYc4iUaI8KD+Xwrz81ULwEmH+pn+PRzUjzHnsMLffAdFPk0M0LFDy/NzEE="

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
