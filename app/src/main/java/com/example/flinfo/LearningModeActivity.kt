package com.example.flinfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flinfo.retrofit.LearningModeResponse
import com.example.flinfo.retrofit.WordInfo
import com.example.flinfo.databinding.ActivityLearningModeBinding
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.core.content.ContextCompat
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.ImageButton
import android.widget.SeekBar


class WordAdapter(
    private val wordInfoList: List<WordInfo>,
    private val onWordClick: (WordInfo) -> Unit
) {
    fun getSpannableText(context: Context): SpannableString {
        val spannableString = SpannableString(wordInfoList.joinToString("") { it.word })
        var startIndex = 0

        wordInfoList.forEach { wordInfo ->
            val word = wordInfo.word
            Log.d("SpannableText", "Processing word: $word")

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onWordClick(wordInfo)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false // Remove the underline
                    ds.color = ContextCompat.getColor(context, R.color.word_color) // Set the desired color
                }
            }
            val spanEndIndex = startIndex + word.length
            Log.d("SpannableText", "Setting span for word: $word, start: $startIndex, end: $spanEndIndex")
            spannableString.setSpan(clickableSpan, startIndex, spanEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            startIndex = spanEndIndex
        }
        return spannableString
    }
}

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
        learningModeResponse.title?.forEach { title ->
            title.word?.let { word ->
                wordInfoList.add(
                    WordInfo(
                        word,
                        title.pinyin,
                        title.meaning,
                        title.nature
                    )
                )
            }
        }

        val titleWordsAdapter = WordAdapter(wordInfoList, ::onWordClick)
        val spannableTitleString = titleWordsAdapter.getSpannableText(this@LearningModeActivity)

        learningModeResponse.sourceArticleText?.forEach { sourceArticleText ->
            sourceArticleText.word?.let { word ->
                wordInfoList.add(
                    WordInfo(
                        word,
                        sourceArticleText.pinyin,
                        sourceArticleText.meaning,
                        sourceArticleText.nature
                    )
                )
            }
        }

        val articleWordsAdapter = WordAdapter(wordInfoList, ::onWordClick)
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
        binding.speakTitleButton.setOnClickListener {
            TextToSpeechHelper.speak(binding.titleTextView.text.toString())
        }

        val speakArticleButton: ImageButton = findViewById(R.id.speak_article_button)
        var isSpeaking = false

        binding.speakArticleButton.setOnClickListener {
            isSpeaking = if (isSpeaking) {
                // Stop the speech
                TextToSpeechHelper.stop()
                speakArticleButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                false
            } else {
                // Start the speech
                val text = binding.paragraphTextView.text.toString()
                TextToSpeechHelper.speak(text)
                speakArticleButton.setImageResource(R.drawable.ic_baseline_pause_24)
                true
            }
        }

        binding.speechRateSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rate = progress.toFloat() / 50.0f // Adjust the range as needed
                TextToSpeechHelper.setSpeechRate(rate)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TextToSpeechHelper.onActivityResult(requestCode, resultCode, data, this)
    }

    private fun onWordClick(wordInfo: WordInfo) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(wordInfo.word)

        val dialogMessage = StringBuilder()
        wordInfo.pinyin?.let {
            dialogMessage.append("Pinyin: $it\n")
        }
        wordInfo.nature?.let {
            dialogMessage.append("Nature: $it\n")
        }
        wordInfo.meaning?.let { meanings ->
            dialogMessage.append("Meanings:\n")
            meanings.forEachIndexed { index, meaning ->
                dialogMessage.append("${index + 1}. $meaning\n")
            }
        }

        dialogBuilder.setMessage(dialogMessage.toString())
        dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        })

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        TextToSpeechHelper.shutdown()
    }
}