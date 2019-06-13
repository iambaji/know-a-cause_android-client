package com.example.knowacause.Utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Pref(ctx : Context?){
companion object {
    private val AUTH_TOKEN ="device_auth_token"
    private val LOGIN_STATUS = "login_status"
    private val USER_EMAIL = "user_email"
}

    private val preferenceses = PreferenceManager.getDefaultSharedPreferences(ctx)

    var loginOrNot = preferenceses.getBoolean(LOGIN_STATUS,false)
    set(value) = preferenceses.edit().putBoolean(LOGIN_STATUS,value).apply();

    var email = preferenceses.getString(USER_EMAIL,"")
    set(value) = preferenceses.edit().putString(USER_EMAIL,value).apply()


    var authLoginToken = preferenceses.getString(AUTH_TOKEN,"")
    set(value) = preferenceses.edit().putString(AUTH_TOKEN,value).apply()

}