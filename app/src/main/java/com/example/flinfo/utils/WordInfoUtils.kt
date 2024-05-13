package com.example.flinfo.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.example.flinfo.retrofit.WordInfo

object WordInfoUtils {
    fun onWordClick(context: Context, wordInfo: WordInfo) {
        val dialogBuilder = AlertDialog.Builder(context)
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
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}