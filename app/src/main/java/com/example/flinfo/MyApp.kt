package com.example.flinfo

import android.app.Application
import com.example.flinfo.retrofit.RetrofitHelper

class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        RetrofitHelper.init(applicationContext)
    }
}