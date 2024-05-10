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
import android.text.style.ClickableSpan
import android.text.method.LinkMovementMethod
import android.util.Log


class WordAdapter(
    private val wordInfoList: List<WordInfo>,
    private val onWordClick: (WordInfo) -> Unit
) {
    fun getSpannableText(): SpannableString {
        val spannableString = SpannableString(wordInfoList.joinToString("") { it.word })
        var startIndex = 0

        wordInfoList.forEach { wordInfo ->
            val word = wordInfo.word
            Log.d("SpannableText", "Processing word: $word")

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onWordClick(wordInfo)
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
        val spannableTitleString = titleWordsAdapter.getSpannableText()

        learningModeResponse.title?.forEach { sourceArticleText ->
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
        val spannableArticleString = articleWordsAdapter.getSpannableText()

        binding.titleTextView.apply {
            text = spannableTitleString
            movementMethod = LinkMovementMethod.getInstance()
        }
        binding.paragraphTextView.apply {
            text = spannableArticleString
            movementMethod = LinkMovementMethod.getInstance()
        }
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
}
