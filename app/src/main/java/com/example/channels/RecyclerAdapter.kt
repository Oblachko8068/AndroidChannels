package com.example.channels

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.channels.FavUtils
class CustomRecyclerAdapter(private var channels: List<Channels>) : RecyclerView
.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newChannels: List<Channels>) {
        channels = newChannels
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setDataStar(newChannels: List<Channels>) {
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
            holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_enable))
        } else {
            holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_disable))
        }
        holder.icon_fav.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // Обновляем состояние избранности для текущей позиции
                channel.fav_selected = !channel.fav_selected

                if (channel.fav_selected) {
                    // Если канал стал избранным, добавляем его позицию в набор
                    FavUtils.favSelectedPositions.add(position)
                    holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_enable))
                } else {
                    // Если канал перестал быть избранным, удаляем его позицию из набора
                    FavUtils.favSelectedPositions.remove(position)
                    holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_disable))
                }
            }
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChannelPlayerActivity::class.java)
            intent.putExtra("channel_name", channel.name) // Передаем название канала в интент
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = channels.size

}