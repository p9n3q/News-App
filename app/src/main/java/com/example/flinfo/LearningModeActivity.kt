package com.example.flinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.flinfo.retrofit.LearningModeResponse
import com.example.flinfo.retrofit.WordInfo
import com.example.flinfo.databinding.ActivityLearningModeBinding
import android.text.method.LinkMovementMethod
import android.content.Intent
import android.graphics.Color
import android.widget.SeekBar
import com.example.flinfo.adapters.WordAdapter
import com.example.flinfo.utils.WordInfoUtils

class LearningModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearningModeBinding
    private lateinit var learningModeResponse: LearningModeResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearningModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TextToSpeechHelper
        TextToSpeechHelper.initialize(this)

        learningModeResponse = intent.getParcelableExtra("learningModeResponse")!!

        val wordInfoList = mutableListOf<WordInfo>()
        fun addWordInfoToList(word: String?, pinyin: String?, meaning: List<String>?, nature: String?) {
            word?.let {
                wordInfoList.add(WordInfo(it, pinyin, meaning, nature))
            }
        }

        learningModeResponse.title?.forEach { title ->
            addWordInfoToList(title.word, title.pinyin, title.meaning, title.nature)
        }

        val titleWordsAdapter = WordAdapter(wordInfoList, WordInfoUtils::onWordClick)
        val spannableTitleString = titleWordsAdapter.getSpannableText(this@LearningModeActivity)

        learningModeResponse.sourceArticleText?.forEach { sourceArticleText ->
            addWordInfoToList(sourceArticleText.word, sourceArticleText.pinyin, sourceArticleText.meaning, sourceArticleText.nature)
        }

        val articleWordsAdapter = WordAdapter(wordInfoList, WordInfoUtils::onWordClick)
        val spannableArticleString = articleWordsAdapter.getSpannableText(this@LearningModeActivity)

        binding.titleTextView.apply {
            text = spannableTitleString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
        binding.paragraphTextView.apply {
            text = spannableArticleString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
        // Set up the speak button for the title
        val titleSpeakButton = ToggleableSpeakButton(this, binding.titleTextView)
        binding.titleSpeakButtonContainer.addView(titleSpeakButton)

        // Set up the speak button for the article
        val articleSpeakButton = ToggleableSpeakButton(this, binding.paragraphTextView)
        binding.articleSpeakButtonContainer.addView(articleSpeakButton)

        binding.speechRateSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rate = progress.toFloat() / 50.0f // Adjust the range as needed
                TextToSpeechHelper.setSpeechRate(rate)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TextToSpeechHelper.onActivityResult(requestCode, resultCode, data, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        TextToSpeechHelper.shutdown()
    }
}