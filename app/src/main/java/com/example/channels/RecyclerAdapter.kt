package com.example.channels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerAdapter(private val channels: List<Channels>) : RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val largeTextView: TextView = itemView.findViewById(R.id.channelName)
        val smallTextView: TextView = itemView.findViewById(R.id.channelDesc)
        val channelIcon: ImageView = itemView.findViewById(R.id.channelIcon)
        val icon_fav: ImageView = itemView.findViewById(R.id.icon_fav)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.channel_block, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val channel = channels[position]
        holder.largeTextView.text = channel.name
        holder.smallTextView.text = channel.description
        holder.channelIcon.setImageResource(channel.iconResource)
        holder.icon_fav.setImageResource(R.drawable.baseline_star_24)
    }

    override fun getItemCount() = channels.size

}