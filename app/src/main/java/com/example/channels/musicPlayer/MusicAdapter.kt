package com.example.channels.musicPlayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.channels.R
import com.example.channels.databinding.MusicViewBinding

class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false
) : RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    private var musicPlayerService: MusicPlayerService? = null
    fun setMusicPlayerService(service: MusicPlayerService?) {
        musicPlayerService = service
    }


    private var itemClickListener: ((position: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (position: Int) -> Unit) {
        itemClickListener = listener
    }

    inner class MyHolder(private val binding: MusicViewBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            itemClickListener?.invoke(adapterPosition)

            // Проверяем, что сервис доступен и плейлист не пустой
            if (musicList.isNotEmpty() && musicPlayerService != null) {
                val selectedMusic = musicList[adapterPosition]
                musicPlayerService?.playMusic(selectedMusic)
            }
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(context)
        val binding = MusicViewBinding.inflate(inflater, parent, false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val currentMusic = musicList[position]

        holder.title.text = currentMusic.title
        holder.album.text = currentMusic.album
        holder.duration.text = formatDuration(currentMusic.duration)

        Glide.with(context)
            .load(currentMusic.artUri)
            //.apply(RequestOptions().placeholder(R.drawable.baseline_star_24)) // Placeholder image
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(newList: ArrayList<Music>) {
        musicList.clear()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatDuration(duration: Long): String {
        // Convert duration from milliseconds to minutes:seconds format
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
