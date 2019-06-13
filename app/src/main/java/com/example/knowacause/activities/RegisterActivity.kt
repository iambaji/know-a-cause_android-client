package com.example.knowacause.activities

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.knowacause.network.KnowaCauseAPI
import com.example.knowacause.network.NetworkModule
import com.example.knowacause.R
import com.example.knowacause.network.User
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }


    fun register_user(v : View)
    {


        val errors : HashMap<String,String> = HashMap()

        if(re_username.text.trim().toString()=="" )
            errors["e_username"] = "Please enter the Username"

        if(re_password.text.trim().toString()=="")
            errors["e_password"] = "Please enter the Password"

        if(re_email.text.trim().toString()=="")
            errors["e_email"] = "Please enter Email Address"

        if(re_ngo_name.text.trim().toString()=="" )
            errors["e_ngoname"] = "Please enter NGO's Name"

        if(re_ngo_phone.text.trim().toString()=="")
            errors["e_ngophone"] = "Please enter NGO Phone Number"

        if(re_ngo_about.text.trim().toString()=="" )
            errors["e_ngoabout"] = "Please enter Something About the NGO"

        if(!errors.isEmpty())
        {
            var error : StringBuilder = StringBuilder()
            errors.forEach{ (key, value) -> error.append(value.toString()+""+"\n")}
            AlertDialog.Builder(this).setTitle("Error").setMessage(error).setPositiveButton("OK", object : DialogInterface.OnClickListener
            {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                   p0?.cancel()
                }
            }).show()

           // snackbar(v,errors.values.toString(),Snackbar.LENGTH_LONG)
        }
        else
        {
            val username = re_username.text.trim().toString();
            val password = re_password.text.trim().toString();
            val email = re_email.text.trim().toString()
            val ngoname = re_ngo_name.text.trim().toString();
            val ngophone = re_ngo_phone.text.trim().toString();
            val ngoabout = re_ngo_about.text.trim().toString();

            val user = User(username = username, password_hash = password, ngoabout = ngoabout, ngoname = ngoname, ngophone = ngophone, email = email)
            //post to api and register
            val retrofitService = NetworkModule.getRetrofit();
            val apiService = retrofitService?.create(KnowaCauseAPI::class.java)
            val progressbar = findViewById<ProgressBar>(R.id.progressbar_register)
            progressbar.visibility = View.VISIBLE
            apiService?.register(user)?.enqueue(object :  retrofit2.Callback<Object> {
                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    progressbar.visibility = View.GONE
                    Toast.makeText(applicationContext,"Call Failure",Toast.LENGTH_LONG).show();
                    Log.d("Retrofit",t?.message.toString())
                }

                override fun onResponse(call: Call<Object>?, response: Response<Object>) {
                    progressbar.visibility = View.GONE
                    if(response.isSuccessful && response.code() == 201)
                    {
                        Toast.makeText(applicationContext,"User Registered, Go back and Login",Toast.LENGTH_LONG).show();
                        startActivity(Intent(this@RegisterActivity,signIn_activity::class.java))
                    }
                    else if(response.code() == 409)
                    {
                        Toast.makeText(applicationContext,"User with email is already registered, go login",Toast.LENGTH_LONG).show();
                    }
                }

            })


        }




    }
}
