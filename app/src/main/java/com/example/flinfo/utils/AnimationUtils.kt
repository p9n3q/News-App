// AnimationUtils.kt
package com.example.flinfo.utils

import android.os.Build
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

fun ImageView.animateImageTransition(imageUrl: String) {
    this.animate().alpha(0f).setDuration(300).withEndAction {
        Glide.with(this.context).load(imageUrl).into(this)
        this.animate().alpha(1f).setDuration(300).start()
    }.start()
}

fun TextView.animateTextTransition(newText: String) {
    this.animate().alpha(0f).setDuration(300).withEndAction {
        this.text = newText
        this.animate().alpha(1f).setDuration(300).start()
    }.start()
}

fun TextView.animateHtmlTextTransition(htmlText: String) {
    val formattedContentHtml = htmlText.replace("\n", "<br>")
    val contentSpanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(formattedContentHtml, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlText)
    }
    this.animate().alpha(0f).setDuration(300).withEndAction {
        this.text = contentSpanned
        this.animate().alpha(1f).setDuration(300).start()
    }.start()
}
