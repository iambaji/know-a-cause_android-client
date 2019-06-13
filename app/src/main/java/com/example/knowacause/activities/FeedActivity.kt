package com.example.knowacause.activities

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.widget.FrameLayout
import android.widget.Toast
import com.example.knowacause.Fragments.HomeFragment
import com.example.knowacause.R
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction


import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar

import com.example.knowacause.Extensions.log
import com.example.knowacause.Fragments.NotificationsFragment
import com.example.knowacause.Fragments.ProfileFragment
import com.example.knowacause.Fragments.YourPosts
import com.example.knowacause.SimpleTextDisplayDialog
import com.example.knowacause.Utils.Pref
import com.example.knowacause.network.*
import kotlinx.android.synthetic.main.notification_item.*
import retrofit2.Call
import retrofit2.Response


class FeedActivity : AppCompatActivity(), HomeFragment.OnListFragmentInteractionListener, YourPosts.OnFragmentInteractionListener
, NotificationsFragment.OnFragmentInteractionListener{

    var activeFragment : Fragment ? =null
    override fun onFragmentInteractionNotification(item : NotificationFromServer) {

    }

    override fun onFragmentInteraction(item: PostFromServer) {
        //Toast.makeText(this,item.post_title.toString(),Toast.LENGTH_LONG).show();
       showNotificationAlert(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.feedactivity_menu,menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
         super.onOptionsItemSelected(item)

       if(item?.itemId == R.id.about_item){
           showAlertDialog()
       }
        if(item?.itemId == R.id.visit_website)
        {
            val url = "http://webportal-183112.appspot.com/"
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        if(item?.itemId == R.id.logout)
        {
            logoutUser()
        }
        return true;
    }

    private fun showAlertDialog() {
        SimpleTextDisplayDialog(this).showTextDisplay("About",resources.getString(R.string.about).toString())
    }
    private val retrofit = NetworkModule.getRetrofit()
    private val retrofitService = retrofit?.create(KnowaCauseAPI::class.java)
    fun logoutUser()
    {
        val pref = Pref(this)
        val authtoken = pref.authLoginToken
      //  progress?.visibility = View.VISIBLE
        val response = retrofitService?.logout("Bearer "+authtoken)?.enqueue(object : retrofit2.Callback<Object>{
            override fun onResponse(call: Call<Object>?, response: Response<Object>) {
                //progress?.visibility = View.GONE

                if((response.isSuccessful) && response.code() == 200)
                {

                }

            }

            override fun onFailure(call: Call<Object>?, t: Throwable?) {
               // progress?.visibility = View.GONE
                Toast.makeText(applicationContext,"Trouble Logging Out!",Toast.LENGTH_LONG).show()
                Log.d("Log","Logged out"+t?.localizedMessage)
            }

        })
        pref.loginOrNot = false
        pref.email = null
        pref.authLoginToken = null
        startActivity(Intent(this,signIn_activity::class.java))
        Toast.makeText(this,"Logged Out!",Toast.LENGTH_LONG).show()

    }
    fun showNotificationAlert(item : PostFromServer)
    {
        Log.d("call","getting calleed")
        val view = layoutInflater.inflate(R.layout.notification_alert_dialog,null);
        val alertDialog = AlertDialog.Builder(this).setTitle("Notify Organizer").setView(view)

                .setMessage("Organizer will get a notification").setPositiveButton("Notify",object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        Log.d("call","onclick")
                        val pref = Pref(applicationContext)
                        val token = pref.authLoginToken
                        val email = pref.email
                        val name = view.findViewById<EditText>(R.id.guest_name_notification_dialog).text.toString()
                        val contact = view.findViewById<EditText>(R.id.guest_contact_notification_dialog).text.toString()
                        val post_id = item.post_id
                        val notificationfromclient = NotificationFromClient(post_id,email,contact,name)
                        val retrofitService = NetworkModule.getRetrofit()?.create(KnowaCauseAPI::class.java)
                        val progressbar = findViewById<ProgressBar>(R.id.progress_feedactivity)
                        progressbar.visibility = View.VISIBLE

                        retrofitService?.saveNotifications("Bearer "+token,notificationfromclient)?.enqueue(object : retrofit2.Callback<NotificationFromServer>{
                            override fun onResponse(call: Call<NotificationFromServer>?, response: Response<NotificationFromServer>) {
                                progressbar.visibility = View.GONE
                                Log.d("responec",response.message())
                                if(response.isSuccessful && response.code() ==201)
                                {
                                    Toast.makeText(applicationContext,"Notified",Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<NotificationFromServer>?, t: Throwable?) {
                                progressbar.visibility = View.GONE
                                Toast.makeText(applicationContext,"Trouble Notifying the User",Toast.LENGTH_SHORT).show()
                                Log.d("Notification", "Trouble Notifying"+t?.localizedMessage)
                            }

                        })
                        p0?.dismiss()
                    }
                })

        alertDialog.show()
    }



    lateinit var navigationView : BottomNavigationView
    lateinit var frame : FrameLayout
    lateinit var container : CoordinatorLayout
    var toolbar : Toolbar? = null
    private val mOnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val fragment: Fragment
            when (item.getItemId()) {
                R.id.home -> {
                    toolbar?.setTitle("Home")
                    val fragment = supportFragmentManager.findFragmentByTag("Home")
                    if(fragment==null)
                    {
                        log("fragment not found")
                        loadFragment(HomeFragment.getInstance(),"Home")
                    }

                    else
                        loadFragment(fragment,"Home")
                    return true
                }
                R.id.your_posts -> {
                    toolbar?.setTitle("Your Posts")
                    val fragment = supportFragmentManager.findFragmentByTag("Your Posts")
                    if(fragment==null){
                        log("fragment not found")
                        loadFragment(YourPosts.getInstance(),"Your Posts")
                    }

                    else
                        loadFragment(fragment,"Your Posts")
                   
                    return true
                }
                R.id.notifications -> {
                    toolbar?.setTitle("Notifications")
                    val fragment = supportFragmentManager.findFragmentByTag("Notifications")
                    if(fragment==null)
                    {
                        log("fragment not found")
                        loadFragment(NotificationsFragment.getInstance(),"Notifications")
                    }

                    else
                        loadFragment(fragment,"Notifications")
                    
                    return true
                }
                R.id.profile -> {
                    toolbar?.setTitle("Profile")
                    val fragment = supportFragmentManager.findFragmentByTag("Profile")
                    if(fragment==null)
                    {log("fragment not found")
                        loadFragment(ProfileFragment.getInstance(),"Profile")}
                    else
                        loadFragment(fragment,"Profile")
                    
                    return true
                }
            }
            return false
        }
    }
    override fun onListFragmentInteraction(item: PostFromServer) {
       //Toast.makeText(this,item.post_title.toString(),Toast.LENGTH_LONG).show();
        showNotificationAlert(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView = findViewById(R.id.bottom_nav)
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        toolbar?.setTitle("Home")
        val homeFragment = HomeFragment.getInstance()

        loadFragment(homeFragment,"Home")
        activeFragment = homeFragment


    }


fun loadFragment(fragment: Fragment,tag : String)
{
    val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.frame,fragment,tag)
    transaction.addToBackStack(null)
    transaction.commit()


}
}
