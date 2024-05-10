package com.example.flinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AlertDialog
import java.util.*

object TextToSpeechHelper : TextToSpeech.OnInitListener {

    private const val TTS_DATA_CHECK_REQUEST_CODE = 1
    private var textToSpeech: TextToSpeech? = null

    interface TextToSpeechCallback {
        fun onTtsInitialized()
        fun onTtsError(errorMessage: String)
    }

    fun initialize(activity: Activity) {
        val ttsIntent = Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
        activity.startActivityForResult(ttsIntent, TTS_DATA_CHECK_REQUEST_CODE)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, context: Context) {
        if (requestCode == TTS_DATA_CHECK_REQUEST_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // TTS data is available, initialize the TextToSpeech object
                textToSpeech = TextToSpeech(context, this)
            } else {
                // TTS data is not available, show installation instructions
                showTtsInstallationInstructions(context)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.CHINESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle missing or unsupported language data
                showTtsLanguageInstructions(MyApp.appContext)
            } else {
                // TextToSpeech is initialized and ready to use
                (MyApp.appContext as? TextToSpeechCallback)?.onTtsInitialized()
            }
        } else {
            // Handle initialization failure
            val errorMessage = "TextToSpeech initialization failed"
            Log.e("TextToSpeechHelper", errorMessage)
            (MyApp.appContext as? TextToSpeechCallback)?.onTtsError(errorMessage)
        }
    }

    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }

    private fun showTtsInstallationInstructions(context: Context) {
        val message = "To enable text-to-speech functionality, please install the required voice data by following these steps:\n\n" +
                "1. Open your device's Settings.\n" +
                "2. Go to 'System' or 'General Management'.\n" +
                "3. Select 'Language and input' or 'Language and keyboard'.\n" +
                "4. Tap on 'Text-to-speech' or 'Text-to-speech output'.\n" +
                "5. Install the desired text-to-speech engine (e.g., Google Text-to-speech Engine).\n" +
                "6. Follow the prompts to download and install the required voice data.\n\n" +
                "Once the installation is complete, please restart the app."

        AlertDialog.Builder(context)
            .setTitle("Text-to-Speech Installation")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                context.startActivity(installIntent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTtsLanguageInstructions(context: Context) {
        val message = "The required language data for Mandarin text-to-speech is not available or not supported.\n\n" +
                "Please follow these steps to install the Mandarin voice data:\n" +
                "1. Open your device's Settings.\n" +
                "2. Go to 'System' or 'General Management'.\n" +
                "3. Select 'Language and input' or 'Language and keyboard'.\n" +
                "4. Tap on 'Text-to-speech' or 'Text-to-speech output'.\n" +
                "5. Tap on 'Install voice data' or 'Language'.\n" +
                "6. Select 'Mandarin (China)' or 'cmn-CN' and download the voice data.\n\n" +
                "Once the voice data is downloaded, please restart the app."

        AlertDialog.Builder(context)
            .setTitle("Mandarin Text-to-Speech")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}