package com.example.quest.utils

import android.app.AlertDialog
import android.content.Context
import android.icu.text.CaseMap

class AlertHelper {
    companion object {
        fun showAlert(context: Context, title: String, msg: String) {
            val builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
            //val alert = builder.create()
            builder.setPositiveButton("OK") { dialog, _ ->
                builder.create().dismiss()
            }
            builder.show()
        }
    }
}