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
import com.example.knowacause.FeedAdapter

import com.example.knowacause.R
import com.example.knowacause.Utils.Pref
import com.example.knowacause.network.PostFromServer
import com.example.knowacause.network.KnowaCauseAPI
import com.example.knowacause.network.NetworkModule
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class YourPosts : Fragment() {
    private var columnCount = 1
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listener: OnFragmentInteractionListener? = null
    lateinit var progressbar : ProgressBar
    lateinit var feedAdapter : FeedAdapter
    lateinit var recycler : RecyclerView
    var is_your_post : Boolean? = false;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    var rootView : View? = null
    var swipeRefreshLayout : SwipeRefreshLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       if(rootView == null)
       {
           rootView = inflater.inflate(R.layout.fragment_home_list, container, false)
           swipeRefreshLayout = rootView?.findViewById(R.id.swipe_refresh)
           swipeRefreshLayout?.setColorSchemeResources(android.R.color.holo_blue_bright,

                   android.R.color.holo_green_light,

                   android.R.color.holo_orange_light,

                   android.R.color.holo_red_light)
           swipeRefreshLayout?.setOnRefreshListener { getPosts() }
           recycler = rootView?.findViewById<RecyclerView>(R.id.list_recycler_view) as RecyclerView
           progressbar = rootView?.findViewById<ProgressBar>(R.id.feedprogressbar) as ProgressBar
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
           getPosts()
           return rootView
       }
        return rootView
    }


    fun getPosts()
    {
        var arrayList : ArrayList<PostFromServer> = ArrayList()
        val retrofit = NetworkModule.getRetrofit()
        val apiservice = retrofit?.create(KnowaCauseAPI::class.java)
        progressbar.visibility = View.VISIBLE


            val pref = Pref(context)
            val email = pref.email
            apiservice?.getOwnposts(email)?.enqueue(object : retrofit2.Callback<List<PostFromServer>>{
                override fun onFailure(call: Call<List<PostFromServer>>?, t: Throwable?) {
                    progressbar.visibility = View.GONE
                    Toast.makeText(context,"Trouble Getting the list, Try Again Later",Toast.LENGTH_LONG).show();
                    Log.d("Feed","FeedActvity: Error onFailure"+ t?.localizedMessage.toString())
                }

                override fun onResponse(call: Call<List<PostFromServer>>?, response: Response<List<PostFromServer>>) {
                    if(response.isSuccessful && response.code() == 200)
                    {
                        progressbar.visibility = View.GONE
                        Log.d("Retrofit","usersposts for : "+email+" are:  "+response.body().toString())
                        if(response.body().size==0)
                        {
                            Toast.makeText(context,"No, Posts to display!", Toast.LENGTH_LONG).show();
                        }
                        else{

                            feedAdapter = FeedAdapter(arrayList, context)
                            recycler.adapter = feedAdapter
                            val size = feedAdapter.post_items.size
                            arrayList?.addAll(response.body())

                            feedAdapter.notifyItemRangeInserted(size,arrayList.size)

                            swipeRefreshLayout?.isRefreshing = false
                        }

                    }
                }

            })



    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(item: PostFromServer) {
        listener?.onFragmentInteraction(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

       is_your_post = arguments?.getBoolean(ARG_PARAM1)
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
        fun onFragmentInteraction(item : PostFromServer)
    }

    companion object {
        private var INSTANCE : YourPosts? = null
        fun getInstance() : YourPosts {
            if(INSTANCE!=null)
            {
                return INSTANCE as YourPosts;
            }
            else{
                return YourPosts()
            }
        }
    }
}
