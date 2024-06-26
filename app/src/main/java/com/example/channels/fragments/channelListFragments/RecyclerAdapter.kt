package com.example.channels.fragments.channelListFragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.channels.R
import com.example.channels.databinding.ChannelBlockBinding
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.domain.model.FavoriteChannel

class RecyclerAdapter(
    private val context: Context,
    private var channels: List<Channel>,
    private var epg: List<Epg>,
    private var favoriteChannel: List<FavoriteChannel>,
    private val itemClickListener: OnChannelItemClickListener,
) : RecyclerView.Adapter<RecyclerAdapter.ChannelViewHolder>() {

    interface OnChannelItemClickListener {

        fun onChannelItemClicked(channel: Channel, epg: Epg)

        fun onFavoriteClicked(channel: Channel)
    }

    private class DiffUtilCallback(
        private val oldChannelList: List<Channel>,
        private val newChannelList: List<Channel>,
        private val oldFavoriteChannelList: List<FavoriteChannel>,
        private val newFavoriteChannelList: List<FavoriteChannel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldChannelList.size

        override fun getNewListSize(): Int = newChannelList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldChannelList[oldItemPosition].javaClass == newChannelList[newItemPosition].javaClass

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldFavorite = oldFavoriteChannelList.any { it.channelId == oldChannelList[oldItemPosition].id }
            val newFavorite = newFavoriteChannelList.any { it.channelId == newChannelList[newItemPosition].id }
            return oldChannelList[oldItemPosition].hashCode() == newChannelList[newItemPosition].hashCode() && oldFavorite == newFavorite
        }
    }

    fun setNewData(newChannelItemList: List<Channel>, newEpgList: List<Epg>) {
        val diffCallback = DiffUtilCallback(channels, newChannelItemList, favoriteChannel, favoriteChannel)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        channels = newChannelItemList
        epg = newEpgList
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateFavoriteChannelList(newChannelItemList: List<Channel>, favChannels: List<FavoriteChannel>) {
        val diffCallback = DiffUtilCallback(channels, newChannelItemList, favoriteChannel, favChannels)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        channels = newChannelItemList
        favoriteChannel = favChannels
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

        fun bind(channelItem: Channel, epgItem: Epg, isFavorite: Boolean, context: Context) {
            Glide.with(context)
                .load(channelItem.image)
                .into(binding.channelIcon)
            binding.channelName.text = channelItem.name
            binding.channelDesc.text = epgItem.title
            binding.iconFav.setImageResource(R.drawable.icon_favorite)
            binding.iconFav.setColorFilter(
                ContextCompat.getColor(context, if (isFavorite) R.color.star_icon_enable else R.color.star_icon_disable)
            )
            binding.iconFav.setOnClickListener {
                binding.iconFav.setColorFilter(
                    ContextCompat.getColor(context, if (isFavorite) R.color.star_icon_enable else R.color.star_icon_disable)
                )
                itemClickListener.onFavoriteClicked(channelItem)
            }
        }
    }

    override fun getItemCount() = channels.size

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channelItem = channels[position]
        val epg = epg.find { it.channelID == channelItem.id }
        val channelExistsInFavList = favoriteChannel.any { it.channelId == channelItem.id }
        epg?.let { holder.bind(channelItem, epg, channelExistsInFavList, context) }
        holder.itemView.setOnClickListener {
            epg?.let {
                itemClickListener.onChannelItemClicked(channelItem, epg)
            }
        }
    }
}