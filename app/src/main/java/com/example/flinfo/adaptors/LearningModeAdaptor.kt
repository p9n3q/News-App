package com.example.flinfo.adaptors

import android.app.AlertDialog
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flinfo.retrofit.Title


class LearningModeAdaptor(private val context: Context, private val words: List<Title>) :
    RecyclerView.Adapter<LearningModeAdaptor.WordViewHolder>() {

    // Add a zero-argument constructor
    constructor() : this(emptyList())

    class WordViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val textView = TextView(parent.context)
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setPadding(8, 8, 8, 8)
        textView.textSize = 18f
        textView.setOnClickListener {
            val position = (it.parent as RecyclerView).getChildAdapterPosition(it)
            if (position != RecyclerView.NO_POSITION) {
                showWordDetails(words[position])
            }
        }
        return WordViewHolder(textView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.textView.text = words[position].word
    }

    override fun getItemCount() = words.size

    private fun showWordDetails(word: Title) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(word.word)
            .setMessage("Pinyin: ${word.pinyin}\n\nMeaning: ${word.meaning?.joinToString("\n")}\n\nSource: ${word.source}")
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }
}
