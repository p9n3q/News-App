package com.example.flinfo.adapters

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class VerticalTextSpan(private val pinyin: String) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val wordWidth = paint.measureText(text, start, end).toInt()
        val pinyinPaint = Paint(paint)
        pinyinPaint.textSize = paint.textSize * 0.6f
        val pinyinWidth = pinyinPaint.measureText(pinyin).toInt()

        if (fm != null) {
            // Adjust the line height to prevent overlap
            fm.ascent = fm.ascent - (paint.textSize * 0.6f).toInt()
            fm.descent = fm.descent + (paint.textSize * 0.3f).toInt()
        }

        return maxOf(wordWidth, pinyinWidth)
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // Draw the word
        canvas.drawText(text!!, start, end, x, y.toFloat(), paint)

        // Draw the pinyin below the word with adjusted margin
        val pinyinPaint = Paint(paint)
        pinyinPaint.textSize = paint.textSize * 0.6f
        pinyinPaint.color = 0xFF888888.toInt() // Adjust color as needed

        // Adjust the y position for the pinyin to reduce the margin
        val pinyinY = y + paint.textSize * 0.8f
        canvas.drawText(pinyin, x, pinyinY, pinyinPaint)
    }
}
