package com.example.knowacause.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.knowacause.Extensions.snackbar
import com.example.knowacause.network.KnowaCauseAPI
import com.example.knowacause.network.NetworkModule
import com.example.knowacause.R
import com.example.knowacause.Utils.Pref
import com.example.knowacause.network.LoginUser
import com.example.knowacause.network.Token


import kotlinx.android.synthetic.main.activity_sign_in_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class signIn_activity : AppCompatActivity() {
    var progressbar : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_activity)
        setSupportActionBar(findViewById(R.id.mytoolbar))
        progressbar = findViewById<ProgressBar>(R.id.progressbar)
        progressbar?.visibility = View.GONE



        }

    override fun onStart() {
        super.onStart()
        val pref = Pref(applicationContext)
        val token = pref.authLoginToken
        val loginstatus = pref.loginOrNot
      /*  NetworkModule.getRetrofit()?.create(KnowaCauseAPI::class.java)?.getLoginStatus("Bearer "+token)?.enqueue(object : retrofit2.Callback<Status>{
            override fun onFailure(call: Call<Status>?, t: Throwable?) {
                Log.d("Retrofit","Login Status: " + t?.localizedMessage.toString())
            }

            override fun onResponse(call: Call<Status>?, response: Response<Status>) {
               if(response.isSuccessful && response.code() == 200)
               {
                   val status = response.body().status
                   Log.d("Retrofit","Status is "+status.toString())
                   if(status)
                   {

                       startActivity(Intent(applicationContext, FeedActivity::class.java))
                   }
                   else
                   {
                       // nothing load up the login activity
                   }
               }
            }

        } )

        */

        if(loginstatus)
        {
            startActivity(Intent(applicationContext, FeedActivity::class.java))
            finish()
        }
    }

    fun login_user(v : View)
    {
        val email = log_username.text.trim().toString()
        val password = log_password.text.trim().toString()
        if(email !="" && password !="")
        {
            loginUser(email,password)
        }
        else
        {
            snackbar(v,"Enter Both Fields",Snackbar.LENGTH_LONG)
        }
    }




    fun register_nav(v : View)
    {
        startActivity(Intent(this, RegisterActivity::class.java))
    }


    fun forgot_user(v : View)
    {
        Toast.makeText(this,"Forgot User! ", Toast.LENGTH_LONG).show()
    }

    fun loginUser( email : String,password : String)
    {
        val userlogin = LoginUser(email, password)
        val retrofitService = NetworkModule.getRetrofit();
        val retrofitAPI = retrofitService?.create(KnowaCauseAPI::class.java)
        progressbar?.visibility = View.VISIBLE
        val loginresponse = retrofitAPI?.login(userlogin)?.enqueue(object : Callback<Token>{
            override fun onFailure(call: Call<Token>?, t: Throwable?) {
                progressbar?.visibility = View.GONE
                Log.d("Retrofit","Login: "+ t.toString())
            }

            override fun onResponse(call: Call<Token>?, response: Response<Token>) {
                progressbar?.visibility = View.GONE
                if(response.isSuccessful && response.code() == 200)
                {
                    val token  = response.body().access_token
                    val pref = Pref(applicationContext)
                    pref.authLoginToken = token
                    pref.loginOrNot = true;
                    pref.email = email
                    startActivity(Intent(applicationContext, FeedActivity::class.java))
                    finish()
                }
                else if(response.code() == 400)
                {
                    Toast.makeText(applicationContext,"Incorrect Password",Toast.LENGTH_LONG).show()
                }
                else if(response.code() == 404)
                {
                    Toast.makeText(applicationContext,"You, need to register first!",Toast.LENGTH_LONG).show()
                }
            }

        })

    }

    }



