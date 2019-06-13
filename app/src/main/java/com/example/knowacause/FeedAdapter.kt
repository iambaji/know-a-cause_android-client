package com.example.knowacause

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.knowacause.Fragments.HomeFragment
import com.example.knowacause.network.PostFromServer
import kotlinx.android.synthetic.main.item_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapter(val post_items: ArrayList<PostFromServer>, val context : Context?) : RecyclerView.Adapter<FeedAdapter.ViewHolder>(){



    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PostFromServer
            (context as HomeFragment.OnListFragmentInteractionListener ).onListFragmentInteraction(item)
        }
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FeedAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view,p0,false))
    }

    override fun getItemCount(): Int {
       return post_items.size
    }

    override fun onBindViewHolder(view: FeedAdapter.ViewHolder, position: Int) {
        val item = post_items[position]
        view.post_title.text = post_items[position].post_title
        view.post_desc.text = post_items[position].post_description
        Log.d("date",post_items[position].date)

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date = sdf.parse(post_items[position].date)

        val display_date = SimpleDateFormat("yyyy-MM-dd, hh:mm a")
        view.post_date.text =display_date.format(date)
        view.post_added_by.text = post_items[position].added_by

        with(view.itemView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val post_title = itemView.post_title
        val post_desc = itemView.post_desc
        val post_date = itemView.post_date
        val post_added_by = itemView.post_added_by
    }

}