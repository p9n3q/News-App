package com.example.flinfo.adapters

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class PinyinSpan(private val pinyin: String) : ReplacementSpan() {

    private val pinyinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 0f
        textAlign = Paint.Align.CENTER
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val chineseWidth = paint.measureText(text, start, end)
        pinyinPaint.textSize = paint.textSize * 0.6f
        val pinyinWidth = pinyinPaint.measureText(pinyin)
        return chineseWidth.coerceAtLeast(pinyinWidth).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val chineseWidth = paint.measureText(text, start, end)
        pinyinPaint.textSize = paint.textSize * 0.6f
        val pinyinWidth = pinyinPaint.measureText(pinyin)

        val totalWidth = chineseWidth.coerceAtLeast(pinyinWidth)
        val chineseX = x + (totalWidth - chineseWidth) / 2
        val pinyinX = x + (totalWidth - pinyinWidth) / 2

        canvas.drawText(text, start, end, chineseX, y.toFloat(), paint)
        canvas.drawText(pinyin, pinyinX, top.toFloat() - paint.textSize * 0.2f, pinyinPaint)
    }
}