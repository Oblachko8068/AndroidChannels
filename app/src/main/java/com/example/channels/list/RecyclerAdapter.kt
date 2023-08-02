package com.example.channels.list

import com.example.channels.list.Channels.Companion.getSavedIntArrayOrFallback
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.channels.ChannelPlayer
import com.example.channels.R

class CustomRecyclerAdapter(private val context: Context, private var channels: List<Channels>) : RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newChannels: List<Channels>) {
        channels = newChannels
        notifyDataSetChanged()
    }

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
        holder.icon_fav.setImageResource(channel.icon_fav)
        if (channel.fav_selected) {
            holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                R.color.icon_enable
            ))
        } else {
            holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                R.color.icon_disable
            ))
        }
        holder.icon_fav.setOnClickListener {
            channel.fav_selected = !channel.fav_selected
            var scetcik = 0
            val catNames = context.resources.getStringArray(R.array.channel_names)
            if (channel.fav_selected) {
                holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                    R.color.icon_enable
                ))
                val intArray = getSavedIntArrayOrFallback(context)

                for (i in catNames.indices) {
                    if(catNames[i]==channel.name){
                        scetcik = i
                    }
                }

                intArray[scetcik] = 1
                Channels.saveIntArray(context, intArray)
            } else {
                holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                    R.color.icon_disable
                ))
                val intArray = getSavedIntArrayOrFallback(context)

                for (i in catNames.indices) {
                    if(catNames[i]==channel.name){
                        scetcik = i
                    }
                }

                intArray[scetcik] = 0
                Channels.saveIntArray(context, intArray)
            }
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChannelPlayer::class.java)

            // Создаем Bundle и помещаем в него данные
            val bundle = Bundle()
            bundle.putString("channel_name", channel.name)
            bundle.putString("channel_description", channel.description)
            bundle.putInt("channel_icon_resource", channel.iconResource)

            // Устанавливаем Bundle как аргумент Intent
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getItemCount() = channels.size

}