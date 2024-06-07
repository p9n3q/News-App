package com.example.flinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flinfo.architecture.NewsViewModel
import com.example.flinfo.retrofit.LearningModeResponse
import com.example.flinfo.utils.GestureListener
import com.example.flinfo.utils.animateHtmlTextTransition
import com.example.flinfo.utils.animateImageTransition
import com.example.flinfo.utils.animateTextTransition
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReadFlinfoNewsActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsData: NewsModel
    private lateinit var newsList: List<NewsModel>
    private var currentPosition: Int = 0

    private lateinit var textView_headline: TextView
    private lateinit var textView_content: TextView
    private lateinit var fabLearningMode: FloatingActionButton

    private lateinit var gestureDetector: GestureDetector

    private var isInLearningMode = false

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_flinfo_news)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        textView_headline = findViewById(R.id.headline)
        textView_content = findViewById(R.id.content)
        fabLearningMode = findViewById(R.id.fab_learning_mode)

        val newsListJson = intent.getStringExtra("news_list")
        if (newsListJson != null) {
            newsList = Gson().fromJson(newsListJson, object : TypeToken<List<NewsModel>>() {}.type)
        } else {
            newsList = emptyList()
        }
        currentPosition = intent.getIntExtra("current_position", 0)

        Log.d("ReadFlinfoNewsActivity", "News List: $newsList")
        Log.d("ReadFlinfoNewsActivity", "Current Position: $currentPosition")

        displayArticle(currentPosition)

        fabLearningMode.setOnClickListener {
            fetchAndOpenLearningMode()
        }

        gestureDetector = GestureDetector(this, GestureListener(
            onSwipeLeft = { navigateToNextArticle() },
            onSwipeRight = { navigateToPreviousArticle() }
        ))
    }

    private fun displayArticle(position: Int) {
        if (position < 0 || position >= newsList.size) {
            Log.e("ReadFlinfoNewsActivity", "Invalid position: $position")
            return
        }

        newsData = newsList[position]
        Log.d("ReadFlinfoNewsActivity", "Displaying article: ${newsData.uuid}")

        title = newsData.headLine

        // Animate the transition
        animateTransition(newsData)
    }

    private fun animateTransition(newsModel: NewsModel) {
        val imageView = findViewById<ImageView>(R.id.imageView)
        val headlineView = findViewById<TextView>(R.id.headline)
        val contentView = findViewById<TextView>(R.id.content)

        // Hide the learning mode button at the start of the transition
        fabLearningMode.hide()

        newsModel.image?.let { imageView.animateImageTransition(it) }
        headlineView.animateTextTransition(newsModel.headLine)
        contentView.animateHtmlTextTransition(newsModel.content ?: "")

        // Show the learning mode button at the end of the transition
        fabLearningMode.show()
    }

    private fun fetchAndOpenLearningMode() {
        val newsUuid = newsList[currentPosition].uuid
        viewModel.getArticleInLearningMode(newsUuid).observe(this, Observer { response ->
            if (response != null) {
                openLearningMode(response)
            }
        })
    }

    private fun openLearningMode(response: LearningModeResponse) {
        isInLearningMode = true
        val intent = Intent(this, LearningModeActivity::class.java)
        intent.putExtra("learningModeResponse", response)
        startActivityForResult(intent, LEARNING_MODE_REQUEST_CODE)
        // Provide feedback to the user
        Snackbar.make(findViewById(android.R.id.content), "Entering Learning Mode", Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (isInLearningMode) {
            exitLearningMode()
        } else {
            super.onBackPressed()
        }
    }

    private fun exitLearningMode() {
        isInLearningMode = false
        // Handle any necessary logic to exit learning mode
        Snackbar.make(findViewById(android.R.id.content), "Exiting Learning Mode", Snackbar.LENGTH_SHORT).show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("GestureDetector", "dispatchTouchEvent: $ev")
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun navigateToNextArticle() {
        Log.d("ReadFlinfoNewsActivity", "Navigating to next article")
        currentPosition = (currentPosition + 1) % newsList.size
        displayArticle(currentPosition)
    }

    private fun navigateToPreviousArticle() {
        Log.d("ReadFlinfoNewsActivity", "Navigating to previous article")
        currentPosition = if (currentPosition - 1 < 0) newsList.size - 1 else currentPosition - 1
        displayArticle(currentPosition)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LEARNING_MODE_REQUEST_CODE && resultCode == RESULT_OK) {
            isInLearningMode = false
        }
    }

    companion object {
        private const val LEARNING_MODE_REQUEST_CODE = 1
    }
}
