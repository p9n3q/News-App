package com.example.flinfo

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.flinfo.architecture.NewsViewModel
import com.example.flinfo.utils.Constants.NEWS_UUID
import java.util.*

class ReadFlinfoNewsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsData: NewsModel
    private lateinit var tts: TextToSpeech

    private lateinit var textView_headline: TextView
    private lateinit var textView_content: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_flinfo_news)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        textView_headline = findViewById(R.id.headline)
        textView_content = findViewById(R.id.content)

        val uuid = intent.getStringExtra(NEWS_UUID)
        if (uuid != null) {
            viewModel.getArticle(uuid).observe(this, androidx.lifecycle.Observer { newsModel ->
                newsData = newsModel
                title = newsModel.headLine
                val imageUrl = newsModel.image
                Glide.with(this).load(imageUrl).into(findViewById(R.id.imageView))
                findViewById<TextView>(R.id.headline).text = newsModel.headLine
                val contentHtml = newsModel.content?:""
                val contentTextView: TextView = findViewById(R.id.content)
                val contentSpanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(contentHtml, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    Html.fromHtml(contentHtml)
                }
                contentTextView.text = contentSpanned
            })
        }

        //text to speech
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.ENGLISH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "TTS Not Supported for this news", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun playNews() {
        tts.speak(newsData.content, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    // Adding voices
    private val voice1: Voice = Voice(
        "en-US-SMTf00",
        Locale("en", "USA"),
        300,
        300,
        false,
        setOf("NA", "f00", "202009152", "female", null)
    )
    private val voice2: Voice = Voice(
        "en-IN-SMTf00",
        Locale("en", "IND"),
        300,
        300,
        false,
        setOf("NA", "f00", "202007071", "female", null)
    )
    private val addedVoices: Set<Voice> = setOf(voice1, voice2)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item_readnewsactivity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.share_news -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, checkout this news : " + newsData.url)
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share with :"))
                return true
            }

            R.id.save_news -> {
                this.let { viewModel.insertNews(this@ReadFlinfoNewsActivity, newsData) }
                Toast.makeText(this, "News saved!", Toast.LENGTH_SHORT)
                    .show()
            }

            R.id.browse_news -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsData.url))
                startActivity(intent)
            }

            // Menu items for vocal news
            R.id.play_news -> {
                playNews()
            }

            R.id.stop_news -> {
                tts.stop()
            }

            R.id.speed_075x -> {
                tts.stop()
                tts.setSpeechRate(0.75F)
                playNews()
            }

            R.id.speed_1x -> {
                tts.stop()
                tts.setSpeechRate(1F)
                playNews()
            }

            R.id.speed_2x -> {
                tts.stop()
                tts.setSpeechRate(2F)
                playNews()
            }

            R.id.voice1 -> {

                tts.stop()
                tts.voice = addedVoices.elementAt(0)
                playNews()

            }

            R.id.voice2 -> {
                tts.stop()
                tts.voice = addedVoices.elementAt(1)
                playNews()
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}