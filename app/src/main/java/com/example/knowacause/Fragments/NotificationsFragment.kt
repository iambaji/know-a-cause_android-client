package com.example.knowacause.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.knowacause.NotificationAdapter

import com.example.knowacause.R
import com.example.knowacause.Utils.Pref
import com.example.knowacause.network.NotificationFromServer
import com.example.knowacause.network.KnowaCauseAPI
import com.example.knowacause.network.NetworkModule
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NotificationsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NotificationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NotificationsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var columnCount = 1
    private var param1: String? = null
    private var param2: String? = null


    lateinit var progressbar : ProgressBar

    lateinit var recycler : RecyclerView
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    var rootView :View? = null
    var swipe_refresh : SwipeRefreshLayout?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
    if(rootView == null)
    {
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false)
        swipe_refresh = rootView?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_notifications)
        recycler = rootView?.findViewById<RecyclerView>(R.id.notification_recycler_view) as RecyclerView
        progressbar = rootView?.findViewById<ProgressBar>(R.id.notification_progressbar) as ProgressBar
        swipe_refresh?.setColorSchemeResources(android.R.color.holo_blue_bright,

                android.R.color.holo_green_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_red_light)
        swipe_refresh?.setOnRefreshListener { getNotifications() }
        // Set the adapter
        if (recycler is RecyclerView) {
            with(recycler) {
                addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
                layoutManager = when {

                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }


            }
        }
        getNotifications()
        return rootView
    }
        return rootView
    }


    fun getNotifications()
    {
        var arrayList : ArrayList<NotificationFromServer> = ArrayList()
        val retrofit = NetworkModule.getRetrofit()
        val apiservice = retrofit?.create(KnowaCauseAPI::class.java)
        val pref = Pref(context)
        val token = pref.authLoginToken
        val email = pref.email
        progressbar.visibility = View.VISIBLE
        Log.d("Notification","Calling method")
        apiservice?.getNotifications("Bearer "+token, email)?.enqueue(object : retrofit2.Callback<List<NotificationFromServer>>{
            override fun onFailure(call: Call<List<NotificationFromServer>>?, t: Throwable?) {
                progressbar.visibility = View.GONE
                Toast.makeText(context,"Trouble Getting the list, Try Again Later",Toast.LENGTH_LONG).show();
                Log.d("Feed","FeedActivity: Error onFailure"+ t?.localizedMessage.toString())
            }

            override fun onResponse(call: Call<List<NotificationFromServer>>?, response: Response<List<NotificationFromServer>>) {
                if(response.isSuccessful && response.code() == 200)
                {


                    Log.d("Retrofit","notification_posts: "+response.body().toString()+" size: "+response.body().size)
                    if(response.body().size==0)
                    {
                        Toast.makeText(context,"No, Notifications to display!",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        val adapter = NotificationAdapter(arrayList, context)
                        recycler.adapter = adapter
                        val last = adapter.notificationsList.size
                        arrayList.addAll(response.body())
                        adapter.notifyItemRangeInserted(last,arrayList.size)
                        swipe_refresh?.isRefreshing = false

                    }
                    progressbar.visibility = View.GONE
                }
                if(response.code() == 500){
                    print("500 error")
                }
            }

        })
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(item: NotificationFromServer) {
        listener?.onFragmentInteractionNotification(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteractionNotification(item : NotificationFromServer)
    }

    companion object {
        private var INSTANCE : NotificationsFragment? = null
        fun getInstance() : NotificationsFragment {
            if(INSTANCE!=null)
            {
                return INSTANCE as NotificationsFragment;
            }
            else{
                return NotificationsFragment()
            }
        }
    }
}
