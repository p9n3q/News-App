package com.example.flinfo

import android.app.Application
import android.content.Context
import com.example.flinfo.retrofit.RetrofitHelper

class MyApp : Application(), TextToSpeechHelper.TextToSpeechCallback {

    companion object {
        lateinit var instance: MyApp
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContext = applicationContext
        RetrofitHelper.init(applicationContext)
    }

    override fun onTtsInitialized() {
        // TextToSpeech is initialized and ready to use
        // You can perform any necessary actions here
    }

    override fun onTtsError(errorMessage: String) {
        // Handle TextToSpeech initialization error
        // You can display an error message or take appropriate action
    }
}