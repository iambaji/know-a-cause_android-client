package com.example.knowacause

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.example.knowacause.activities.FeedActivity

class SimpleTextDisplayDialog(val context : Context){
    fun showTextDisplay(title : String,body : String){
        AlertDialog.Builder(context as FeedActivity).setTitle(title)
                .setMessage(body)
                .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->

                    dialog.dismiss()
                }).show()
    }
}