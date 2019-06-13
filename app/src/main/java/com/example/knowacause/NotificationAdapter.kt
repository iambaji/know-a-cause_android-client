package com.example.knowacause

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.knowacause.network.NotificationFromServer
import kotlinx.android.synthetic.main.notification_item.view.*

class NotificationAdapter(val notificationsList : List<NotificationFromServer>, val ctx : Context?) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
   val view=  LayoutInflater.from(ctx).inflate(R.layout.notification_item,p0,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return notificationsList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
       val item = notificationsList.get(position)
        viewHolder.notification_guest_contact.text = item.notification_guest_contact
        viewHolder.notification_guest_name.text = item.notification_guest_name
        viewHolder.post_id.text = item.notification_post_id
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val notification_guest_name = itemView.guest_name
        val notification_guest_contact = itemView.guest_contact
        val post_id = itemView.post_id
    }
}