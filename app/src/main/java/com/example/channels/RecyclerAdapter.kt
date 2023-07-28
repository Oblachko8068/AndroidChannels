package com.example.channels

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerAdapter(private var channels: List<Channels>) : RecyclerView
.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newChannels: List<Channels>) {
        channels = newChannels
        notifyDataSetChanged()
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val largeTextView: TextView = itemView.findViewById(R.id.channelName)
        val smallTextView: TextView = itemView.findViewById(R.id.channelDesc)
        val channelIcon: ImageView = itemView.findViewById(R.id.channelIcon)
        val icon_fav: ImageView = itemView.findViewById(R.id.iconFav)

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
        holder.icon_fav.setImageResource(channel.icon_fav)
        if (channel.fav_selected) {
            holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_enable))
        } else {
            holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_disable))
        }
        holder.icon_fav.setOnClickListener {
            channel.fav_selected = !channel.fav_selected
            if (channel.fav_selected) {
                addChannel(position)
                holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_enable))
            } else {
                holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_disable))
            }
        }
        holder.itemView.setOnClickListener {
        }
    }

    private fun addChannel(position: Int) {
        val channelToUpdate = channels[position]
        channelToUpdate.fav_selected = true
    }


    override fun getItemCount() = channels.size

}