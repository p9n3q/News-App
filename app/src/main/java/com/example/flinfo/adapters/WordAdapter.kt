package com.example.flinfo.adapters

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.flinfo.R
import com.example.flinfo.retrofit.WordInfo
import kotlin.reflect.KFunction2

class WordAdapter(
    private val wordInfoList: List<WordInfo>,
    private val onWordClick: KFunction2<Context, WordInfo, Unit>
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