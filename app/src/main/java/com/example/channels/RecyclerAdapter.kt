package com.example.channels

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.channels.databinding.ChannelBlockBinding
import com.example.channels.viewModel.ChannelItem
import com.example.domain.model.Channel
import com.example.domain.model.Epg

class RecyclerAdapter(
    private val context: Context,
    private var channelItems: List<ChannelItem>,
    private var epg: List<Epg>,
    private val itemClickListener: OnChannelItemClickListener,
) : RecyclerView.Adapter<RecyclerAdapter.ChannelViewHolder>() {

    interface OnChannelItemClickListener {
        fun onChannelItemClicked(channel: Channel, epg: Epg)
        fun onFavoriteClicked(channel: ChannelItem)
    }

    private class DiffUtilCallback(
        private val oldList: List<ChannelItem>,
        private val newList: List<ChannelItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.javaClass == newItem.javaClass
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    fun setNewData(newChannelItemList: List<ChannelItem>, newEpgList: List<Epg>) {
        val diffCallback = DiffUtilCallback(channelItems, newChannelItemList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        channelItems = newChannelItemList
        epg = newEpgList
        diffResult.dispatchUpdatesTo(this)
    }

    fun filterChannels(filteredChannelList: List<ChannelItem>) {
        val diffCallback = DiffUtilCallback(channelItems, filteredChannelList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        channelItems = filteredChannelList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding =
            ChannelBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelViewHolder(binding, itemClickListener)
    }

    class ChannelViewHolder(
        private val binding: ChannelBlockBinding,
        private val itemClickListener: OnChannelItemClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(channelItem: ChannelItem, epgItem: Epg, context: Context) {
            Glide.with(context)
                .load(channelItem.channel.image)
                .into(binding.channelIcon)
            binding.channelName.text = channelItem.channel.name
            binding.channelDesc.text = epgItem.title
            binding.iconFav.setImageResource(R.drawable.baseline_star_24)
            if (channelItem.isFavorite) {
                binding.iconFav.setColorFilter(
                    ContextCompat.getColor(context, R.color.icon_enable)
                )
            } else {
                binding.iconFav.setColorFilter(
                    ContextCompat.getColor(context, R.color.icon_disable)
                )
            }
            binding.iconFav.setOnClickListener {
                if (channelItem.isFavorite) {
                    binding.iconFav.setColorFilter(
                        ContextCompat.getColor(context, R.color.icon_disable)
                    )
                } else {
                    binding.iconFav.setColorFilter(
                        ContextCompat.getColor(context, R.color.icon_enable)
                    )
                }
                itemClickListener.onFavoriteClicked(channelItem)
            }
        }
    }

    override fun getItemCount() = channelItems.size

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channelItem = channelItems[position]
        val epg = epg.find { it.channelID == channelItem.channel.id }
        if (epg != null) {
            holder.bind(channelItem, epg, context)
        }

        holder.itemView.setOnClickListener {
            if (epg != null) {
                itemClickListener.onChannelItemClicked(channelItem.channel, epg)
            }
        }
    }
}