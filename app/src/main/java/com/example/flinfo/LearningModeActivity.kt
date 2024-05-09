package com.example.flinfo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flinfo.R
import com.example.flinfo.adaptors.LearningModeAdaptor
import com.example.flinfo.retrofit.LearningModeResponse

class LearningModeActivity : AppCompatActivity() {

    private lateinit var learningModeResponse: LearningModeResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_mode)

        learningModeResponse = intent.getParcelableExtra("learningModeResponse")!!

        // Set the headline text
        val headlineTextView = findViewById<TextView>(R.id.headlineTextView)
        headlineTextView.text = learningModeResponse.originalTitle

        // Set up the RecyclerView for the article text
        val articleRecyclerView = findViewById<RecyclerView>(R.id.articleRecyclerView)
        articleRecyclerView.layoutManager = LinearLayoutManager(this)
        articleRecyclerView.adapter = LearningModeAdaptor(this, learningModeResponse.title ?: emptyList())
    }

    // ...
}
