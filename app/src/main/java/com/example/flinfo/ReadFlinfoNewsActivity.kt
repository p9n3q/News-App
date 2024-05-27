package com.example.flinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.flinfo.architecture.NewsViewModel
import com.example.flinfo.utils.Constants.NEWS_UUID

class ReadFlinfoNewsActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsData: NewsModel
    private lateinit var uuidList: ArrayList<String>
    private var currentPosition: Int = 0

    private lateinit var textView_headline: TextView
    private lateinit var textView_content: TextView
    private lateinit var learningModeButton: Button

    private lateinit var gestureDetector: GestureDetector
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

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

        uuidList = intent.getStringArrayListExtra("uuid_list") ?: arrayListOf()
        currentPosition = intent.getIntExtra("current_position", 0)

        Log.d("ReadFlinfoNewsActivity", "UUID List: $uuidList")
        Log.d("ReadFlinfoNewsActivity", "Current Position: $currentPosition")

        displayArticle(currentPosition)

        learningModeButton.setOnClickListener {
            openLearningMode()
        }

        gestureDetector = GestureDetector(this, this)
    }

    private fun displayArticle(position: Int) {
        if (position < 0 || position >= uuidList.size) {
            Log.e("ReadFlinfoNewsActivity", "Invalid position: $position")
            return
        }

        val uuid = uuidList[position]
        Log.d("ReadFlinfoNewsActivity", "Fetching article with UUID: $uuid")

        viewModel.getArticle(uuid).observe(this, Observer { newsModel ->
            if (newsModel != null) {
                newsData = newsModel
                Log.d("ReadFlinfoNewsActivity", "Article fetched: $newsData")
                title = newsModel.headLine

                // Animate the transition
                animateTransition(newsModel)
            } else {
                Log.e("ReadFlinfoNewsActivity", "Failed to fetch article for UUID: $uuid")
            }
        })
    }

    private fun animateTransition(newsModel: NewsModel) {
        val imageView = findViewById<View>(R.id.imageView)
        val headlineView = findViewById<TextView>(R.id.headline)
        val contentView = findViewById<TextView>(R.id.content)

        imageView.animate().alpha(0f).setDuration(300).withEndAction {
            Glide.with(this).load(newsModel.image).into(imageView as ImageView)
            imageView.animate().alpha(1f).setDuration(300).start()
        }.start()

        headlineView.animate().alpha(0f).setDuration(300).withEndAction {
            headlineView.text = newsModel.headLine
            headlineView.animate().alpha(1f).setDuration(300).start()
        }.start()

        contentView.animate().alpha(0f).setDuration(300).withEndAction {
            val contentHtml = newsModel.content ?: ""
            val formattedContentHtml = contentHtml.replace("\n", "<br>")
            val contentSpanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(formattedContentHtml, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(contentHtml)
            }
            contentView.text = contentSpanned
            contentView.animate().alpha(1f).setDuration(300).start()
        }.start()

        learningModeButton.visibility = View.VISIBLE

        // Fetch learning mode data asynchronously and store it
        viewModel.getArticleInLearningMode(newsModel.uuid).observe(this, Observer { response ->
            if (response != null) {
                viewModel.fetchLearningModeData(newsModel.uuid)
            }
        })
    }

    private fun openLearningMode() {
        viewModel.learningModeData.observe(this, Observer { response ->
            if (response != null) {
                val intent = Intent(this, LearningModeActivity::class.java)
                intent.putExtra("learningModeResponse", response)
                startActivity(intent)
            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("GestureDetector", "dispatchTouchEvent: $ev")
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onDown(e: MotionEvent): Boolean {
        Log.d("GestureDetector", "onDown")
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.d("GestureDetector", "onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.d("GestureDetector", "onSingleTapUp")
        return true
    }

    override fun onScroll(
        e1: MotionEvent, e2: MotionEvent,
        distanceX: Float, distanceY: Float
    ): Boolean {
        Log.d("GestureDetector", "onScroll")
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        Log.d("GestureDetector", "onLongPress")
    }

    override fun onFling(
        e1: MotionEvent, e2: MotionEvent,
        velocityX: Float, velocityY: Float
    ): Boolean {
        Log.d("GestureDetector", "onFling: e1=$e1, e2=$e2, velocityX=$velocityX, velocityY=$velocityY")
        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Swipe Right
                    Log.d("GestureDetector", "Swipe Right")
                    navigateToNextArticle()
                } else {
                    // Swipe Left
                    Log.d("GestureDetector", "Swipe Left")
                    navigateToPreviousArticle()
                }
                return true
            }
        }
        return false
    }

    private fun navigateToNextArticle() {
        Log.d("ReadFlinfoNewsActivity", "Navigating to next article")
        currentPosition++
        if (currentPosition < uuidList.size) {
            displayArticle(currentPosition)
        } else {
            currentPosition--  // Stay at the last article if there is no next article
            Log.d("ReadFlinfoNewsActivity", "Reached the last article, cannot navigate further")
        }
    }

    private fun navigateToPreviousArticle() {
        Log.d("ReadFlinfoNewsActivity", "Navigating to previous article")
        currentPosition--
        if (currentPosition >= 0) {
            displayArticle(currentPosition)
        } else {
            currentPosition++  // Stay at the first article if there is no previous article
            Log.d("ReadFlinfoNewsActivity", "Reached the first article, cannot navigate further")
        }
    }
}
