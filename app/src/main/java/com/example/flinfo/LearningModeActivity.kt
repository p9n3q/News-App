package com.example.flinfo

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.flinfo.retrofit.LearningModeResponse
import com.example.flinfo.retrofit.WordInfo
import com.example.flinfo.databinding.ActivityLearningModeBinding
import com.example.flinfo.databinding.LearningModeBottomButtonBarBinding
import com.example.flinfo.databinding.LearningModeParagraphButtonSectionBinding
import com.example.flinfo.databinding.LearningModeTitleSectionBinding
import android.text.method.LinkMovementMethod
import android.graphics.Color
import android.widget.SeekBar
import com.example.flinfo.adapters.WordAdapter
import com.example.flinfo.utils.WordInfoUtils

@Suppress("DEPRECATION")
class LearningModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearningModeBinding
    private lateinit var titleSectionBinding: LearningModeTitleSectionBinding
    private lateinit var buttonSectionBinding: LearningModeParagraphButtonSectionBinding
    private lateinit var learningModeResponse: LearningModeResponse
    private lateinit var wordInfoList: MutableList<WordInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearningModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TextToSpeechHelper
        TextToSpeechHelper.initialize(this)

        learningModeResponse = intent.getParcelableExtra("learningModeResponse")!!

        wordInfoList = mutableListOf()
        fun addWordInfoToList(word: String?, pinyin: String?, meaning: List<String>?, nature: String?) {
            word?.let {
                wordInfoList.add(WordInfo(it, pinyin, meaning, nature))
            }
        }

        learningModeResponse.title?.forEach { title ->
            addWordInfoToList(title.word, title.pinyin, title.meaning, title.nature)
        }

        val titleWordsAdapter = WordAdapter(wordInfoList, WordInfoUtils::onWordClick, false)
        val spannableTitleString = titleWordsAdapter.getSpannableText(this@LearningModeActivity)

        learningModeResponse.sourceArticleText?.forEach { sourceArticleText ->
            addWordInfoToList(sourceArticleText.word, sourceArticleText.pinyin, sourceArticleText.meaning, sourceArticleText.nature)
        }

        // Inflate the included layout using the container
        titleSectionBinding = LearningModeTitleSectionBinding.bind(binding.root.findViewById(R.id.learning_mode_title_section))
        buttonSectionBinding = LearningModeParagraphButtonSectionBinding.bind(binding.root.findViewById(R.id.learning_mode_button_section))

        titleSectionBinding.titleTextView.apply {
            text = spannableTitleString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }

        // Set up the speak button for the article
        val articleSpeakButton = ToggleableSpeakButton(this, binding.paragraphTextView)
        buttonSectionBinding.articleSpeakButtonContainer.addView(articleSpeakButton)

        buttonSectionBinding.speechRateSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rate = progress.toFloat() / 50.0f // Adjust the range as needed
                TextToSpeechHelper.setSpeechRate(rate)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set up the pinyin_switch listener
        buttonSectionBinding.pinyinSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateArticleText(isChecked)
        }

        // Initial setup
        updateArticleText(buttonSectionBinding.pinyinSwitch.isChecked)
    }

    private fun updateArticleText(showPinyin: Boolean) {
        val articleWordsAdapter = WordAdapter(wordInfoList, WordInfoUtils::onWordClick, showPinyin)
        val spannableArticleString = articleWordsAdapter.getSpannableText(this@LearningModeActivity)

        binding.paragraphTextView.apply {
            text = spannableArticleString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
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

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
    }
}
