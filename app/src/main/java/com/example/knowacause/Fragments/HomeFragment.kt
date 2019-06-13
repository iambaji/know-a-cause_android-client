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
import com.example.knowacause.Extensions.log
import com.example.knowacause.FeedAdapter
import com.example.knowacause.R

import com.example.knowacause.network.PostFromServer
import com.example.knowacause.network.KnowaCauseAPI
import com.example.knowacause.network.NetworkModule
import kotlinx.android.synthetic.main.fragment_home_list.*
import retrofit2.Call
import retrofit2.Response
import kotlin.math.log


class HomeFragment() : Fragment() {




    // TODO: Customize parameters
    private var columnCount = 1
    lateinit var progressbar : ProgressBar
    lateinit var feedAdapter : FeedAdapter
    lateinit var recycler : RecyclerView
    private var listener: OnListFragmentInteractionListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")

    }


    var rootView : View? = null
    var swipe_refresh : SwipeRefreshLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
if(rootView == null)
{
    rootView= inflater.inflate(R.layout.fragment_home_list, container, false)

    recycler = rootView?.findViewById<RecyclerView>(R.id.list_recycler_view) as RecyclerView
    progressbar = rootView?.findViewById<ProgressBar>(R.id.feedprogressbar) as ProgressBar
    swipe_refresh = rootView?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh) as SwipeRefreshLayout
    swipe_refresh?.setColorSchemeResources(android.R.color.holo_blue_bright,

            android.R.color.holo_green_light,

            android.R.color.holo_orange_light,

            android.R.color.holo_red_light)

    swipe_refresh?.setOnRefreshListener { refresh() }
    log("onCreateView")
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


    fun refresh()
    {
        getPosts()

    }
    fun getPosts()
    {
        val arrayList = ArrayList<PostFromServer>()
        val retrofit = NetworkModule.getRetrofit()
        val apiservice = retrofit?.create(KnowaCauseAPI::class.java)
        progressbar.visibility = View.VISIBLE
        apiservice?.getUsersPosts()?.enqueue(object : retrofit2.Callback<List<PostFromServer>>{
            override fun onFailure(call: Call<List<PostFromServer>>?, t: Throwable?) {
                progressbar.visibility = View.GONE
                Log.d("Feed","FeedActvity: Error onFailure"+ t?.localizedMessage.toString())
            }

            override fun onResponse(call: Call<List<PostFromServer>>?, response: Response<List<PostFromServer>>) {
                if(response.isSuccessful && response.code() == 200)
                {
                    progressbar.visibility = View.GONE
                    Log.d("Retrifit","usersposts: "+response.body().toString())
                    feedAdapter = FeedAdapter(arrayList, context)
                    recycler.adapter = feedAdapter
                    val size = feedAdapter.post_items.size
                    arrayList?.addAll(response.body())

                    feedAdapter.notifyItemRangeInserted(size,arrayList.size)

                    swipe_refresh?.isRefreshing = false
                }
            }

        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener") as Throwable
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
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: PostFromServer)
    }

    companion object {
        private var INSTANCE : HomeFragment? = null
      fun getInstance() : HomeFragment {
          if(INSTANCE!=null)
          {
              return INSTANCE as HomeFragment;
          }
          else{
              return HomeFragment()
          }
      }
    }
}
