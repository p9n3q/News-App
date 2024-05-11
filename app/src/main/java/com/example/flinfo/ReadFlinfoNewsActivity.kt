package com.example.flinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.flinfo.architecture.NewsViewModel
import com.example.flinfo.utils.Constants.NEWS_UUID
import androidx.lifecycle.Observer

class ReadFlinfoNewsActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsData: NewsModel

    private lateinit var textView_headline: TextView
    private lateinit var textView_content: TextView
    private lateinit var learningModeButton: Button

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_flinfo_news)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        textView_headline = findViewById(R.id.headline)
        textView_content = findViewById(R.id.content)
        learningModeButton = findViewById(R.id.learningModeButton)

        val uuid = intent.getStringExtra(NEWS_UUID)
        if (uuid != null) {
            viewModel.getArticle(uuid).observe(this, Observer { newsModel ->
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
                learningModeButton.visibility = View.VISIBLE
            })
        }

        learningModeButton.setOnClickListener {
            openLearningMode()
        }
    }

    private fun openLearningMode() {
        val uuid = intent.getStringExtra(NEWS_UUID)
        if (uuid != null) {
            viewModel.getArticleInLearningMode(uuid).observe(this, Observer { response ->
                val intent = Intent(this, LearningModeActivity::class.java)
                intent.putExtra("learningModeResponse", response)
                startActivity(intent)
            })
        }
    }
}