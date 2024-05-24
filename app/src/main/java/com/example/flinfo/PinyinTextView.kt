package com.example.flinfo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class PinyinTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var chineseText: String = ""
    private var pinyinText: String = ""
    private var showPinyin: Boolean = false

    fun setChineseText(chinese: String) {
        chineseText = chinese
        invalidate()
    }

    fun setPinyinText(pinyin: String) {
        pinyinText = pinyin
        invalidate()
    }

    fun setShowPinyin(show: Boolean) {
        showPinyin = show
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (showPinyin) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = textSize * 0.6f
            paint.color = currentTextColor
            paint.textAlign = Paint.Align.CENTER

            val chineseChars = chineseText.toCharArray()
            val pinyinParts = pinyinText.split(" ")

            var x = 0f
            for (i in chineseChars.indices) {
                val chineseChar = chineseChars[i].toString()
                val pinyinPart = if (i < pinyinParts.size) pinyinParts[i] else ""

                canvas.drawText(chineseChar, x + textSize / 2, baseline.toFloat(), paint)
                canvas.drawText(pinyinPart, x + textSize / 2, baseline.toFloat() - textSize * 0.8f, paint)

                x += textSize
            }
        }
    }
}