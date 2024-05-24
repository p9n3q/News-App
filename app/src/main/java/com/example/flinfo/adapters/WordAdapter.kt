package com.example.flinfo.adapters

import android.content.Context
import android.text.Layout
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AlignmentSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.example.flinfo.R
import com.example.flinfo.retrofit.WordInfo
class WordAdapter(
    private val wordInfoList: List<WordInfo>,
    private val onWordClick: (Context, WordInfo) -> Unit,
    private val showPinyin: Boolean
) {
    fun getSpannableText(context: Context): SpannableString {
        val spannableString = SpannableStringBuilder()

        wordInfoList.forEach { wordInfo ->
            val word = wordInfo.word
            val pinyin = wordInfo.pinyin

            val startIndex = spannableString.length
            spannableString.append(word)
            val endIndex = spannableString.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onWordClick(context, wordInfo)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(context, R.color.word_color)
                }
            }
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (showPinyin && pinyin != null) {
                spannableString.append(" ")

                val customSpan = VerticalTextSpan(pinyin)
                spannableString.setSpan(customSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return SpannableString(spannableString)
    }
}

