package com.example.flinfo.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private const val BASE_URL = "https://api.flinfo.com/"
    private const val AUTHORIZATION_TOKEN = "QdwUV+8E6feZTqULb1Pf9sLHKLCnp8M43Puyq/q0s+6a/K7a/CWJTRAhfHKaidBYz91RiRdC1v5KB9q4ht1Wy2AL6kwp2occw7T+aSiMv6Ta0elAkA0TlW6P+Lpe04135UgY+Gvq4rMF/rOpuFeB9+XHPAgAxpo15jaMuszLCS0IsPQg38PD17pCXHnbvYTMKlgpgmC9pZbU2Hl5mxDwNAe5p9DXZHTwcRRUVhqknZ/Sn70XuOAvk7b5UebFKjedMNdcOwVwA7nVT8C7hQrhTe/X0scWt2O0TnNOnFL3qdI5vjzjbSQ82mg8iZnG2//N5pAkPMeBENL7KBuDzXhJV0OdlOKRe1elT5bDytxr2wiebJMpZlomtiM+8Jx2eZz0orbLsLmCiDMzmV+usc2yVfkjXrB5HOuA6GP/ZDb4AuZ+Br3EzGEV1iQjyhYE9EmItBC4BlPwQFb5FIIQax4ro8T2MLU13QrdNj16TgihNtPevVP/3IdR0FcJasW3ddXqS5fWgAKZXoBf2J4TTF7RJY626XX9brh6uybrSiKQry3O/BBd1kTpHsV4lGmwzg0v+9tc84ZrDA0I/hFAIih8XFe1wAoeZWYJDCUXGYw+z5v/CTlw4rJ0bCge4RNa2VySl5TekkT/PLBaZManyh49/RIecBd5mfFUt4VE/3Z12SY="

    fun getInstance(): Retrofit {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer $AUTHORIZATION_TOKEN")
                    val request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}
