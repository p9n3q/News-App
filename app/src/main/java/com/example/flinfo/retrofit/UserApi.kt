package com.example.flinfo.retrofit

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("authenticate")
    fun authenticate(
        @Body requestBody: RequestBody
    ): Call<AuthenticationResponse>
}