package com.example.knowacause.Extensions

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

fun AppCompatActivity.snackbar(v : View, msg : String, len : Int)
{
    Snackbar.make(v,msg,len).show()
}

fun Fragment.log(msg : String)
{
    Log.d("log",msg)
}


fun AppCompatActivity.log(msg : String)
{
    Log.d("log",msg)
}

