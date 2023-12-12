package com.example.channels

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.channels.databinding.ChannelBlockBinding
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.domain.repository.FavoriteChannelsRepository

class RecyclerAdapter(
    private val context: Context,
    private var channel: List<Channel>,
    private var epg: List<Epg>,
    private val itemClickListener: OnChannelItemClickListener,
    private val favoriteChannelsRepository: FavoriteChannelsRepository
) : RecyclerView.Adapter<RecyclerAdapter.ChannelViewHolder>() {

    interface OnChannelItemClickListener {
        fun onChannelItemClicked(channel: Channel, epg: Epg)
        fun onFavoriteClicked(channel: Channel)
    }

    private class DiffUtilCallback(
        private val oldList: List<Channel>,
        private val newList: List<Channel>
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

    fun setNewData(newChannelList: List<Channel>, newEpgList: List<Epg>) {
        val diffCallback = DiffUtilCallback(channel, newChannelList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        channel = newChannelList
        epg = newEpgList
        diffResult.dispatchUpdatesTo(this)
    }

    fun filterChannels(filteredChannelList: List<Channel>) {
        val diffCallback = DiffUtilCallback(channel, filteredChannelList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        channel = filteredChannelList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding =
            ChannelBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelViewHolder(binding, itemClickListener, favoriteChannelsRepository)
    }

    class ChannelViewHolder(
        private val binding: ChannelBlockBinding,
        private val itemClickListener: OnChannelItemClickListener,
        private val favoriteRepository: FavoriteChannelsRepository,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(channelItem: Channel, epgItem: Epg, context: Context) {
            Glide.with(context)
                .load(channelItem.image)
                .into(binding.channelIcon)
            binding.channelName.text = channelItem.name
            binding.channelDesc.text = epgItem.title
            binding.iconFav.setImageResource(R.drawable.baseline_star_24)
            binding.iconFav.setColorFilter(
                ContextCompat.getColor(context, R.color.icon_disable)
            )
            val favoriteChannelsArray = favoriteRepository.getSavedFavChannelsArray()
            if (channelItem.id in favoriteChannelsArray) {
                binding.iconFav.setColorFilter(
                    ContextCompat.getColor(context, R.color.icon_enable)
                )
            }
            binding.iconFav.setOnClickListener {
                if (favoriteRepository.isChannelFavorite(channelItem.id)) {
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

    override fun getItemCount() = channel.size

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channel[position]
        val epg = epg.find { it.channelID == channel.id }
        if (epg != null) {
            holder.bind(channel, epg, context)
        }

        holder.itemView.setOnClickListener {
            if (epg != null) {
                itemClickListener.onChannelItemClicked(channel, epg)
            }
        }
    }
}