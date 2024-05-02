package com.example.channels.musicPlayer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.channels.databinding.MusicViewBinding
import com.example.domain.model.Music

class MusicAdapter(
    private val context: Context,
    private var musicList: List<Music>,
    private val itemClickListener: OnMusicItemClickListener,
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    interface OnMusicItemClickListener {

        fun onMusicItemClicked(musicPosition: Int)
    }

    private class DiffUtilCallback(
        private val oldMusicList: List<Music>,
        private val newMusicList: List<Music>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldMusicList.size

        override fun getNewListSize(): Int = newMusicList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldMusicList[oldItemPosition].javaClass == newMusicList[newItemPosition].javaClass

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldMusicList[oldItemPosition].hashCode() == newMusicList[newItemPosition].hashCode()
        }
    }

    fun updateData(newMusicList: List<Music>) {
        val diffUtilCallback = DiffUtilCallback(musicList, newMusicList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        musicList = newMusicList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = MusicViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)
    }

    inner class MusicViewHolder(private val binding: MusicViewBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(currentMusic: Music, context: Context) {
            Glide.with(context)
                .load(currentMusic.artUri)
                .transform(RoundedCorners(18))
                .into(binding.imageMV)
            binding.songNameMV.text = currentMusic.title
            binding.songAlbumMV.text = currentMusic.album
            binding.songDuration.text = formatDuration(currentMusic.duration)
        }
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val currentMusic = musicList[position]
        holder.bind(currentMusic, context)
        holder.itemView.setOnClickListener {
            itemClickListener.onMusicItemClicked(position)
        }
    }

    override fun getItemCount(): Int = musicList.size

    private fun formatDuration(duration: Long): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}