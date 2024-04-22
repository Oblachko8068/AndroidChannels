package com.example.channels.musicPlayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.channels.R
import com.example.channels.databinding.FragmentMusicListBinding
import com.example.domain.model.Music
import java.io.File
import kotlin.concurrent.thread

class MusicListFragment : Fragment(), MusicAdapter.OnMusicItemClickListener {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicPlayerService: MusicPlayerService
    private lateinit var connection: ServiceConnection

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.miniPlayerContainer.visibility = View.GONE
        initServiceConnection()
        binding.miniPlayerContainer.setOnClickListener {
            val miniPlayerFragment = MusicPlayerFragment(musicPlayerService.getCurrentMusic())
            val fragmentManager = requireActivity().supportFragmentManager
            miniPlayerFragment.show(fragmentManager, miniPlayerFragment.tag)
        }
        binding.startStopMusic.setOnClickListener {
            togglePlayer()
        }
        binding.nextMusic.setOnClickListener {
            musicPlayerService.playNext()
        }
        binding.prevMusic.setOnClickListener {
            musicPlayerService.playPrevious()
        }
    }

    private fun initServiceConnection() {
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicPlayerService.MusicBinder
                musicPlayerService = binder.service
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestRuntimePermission()
                    initializeLayout()
                }
                musicPlayerService.currentMusicPositionLiveData.observe(viewLifecycleOwner) {
                    applyMiniPlayer(musicPlayerService.getCurrentMusic())
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }
        val serviceIntent = Intent(requireActivity(), MusicPlayerService::class.java)
        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun togglePlayer() {
        if (musicPlayerService.isPlaying) {
            musicPlayerService.pausePlayer()
            binding.startStopMusic.setImageResource(R.drawable.music_play)
        } else {
            musicPlayerService.startPlayer()
            binding.startStopMusic.setImageResource(R.drawable.music_pause)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                13
            )
        } else {
            initializeLayout()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {
        thread {
            musicPlayerService.musicListMA = getAllAudio()
            requireActivity().runOnUiThread {
                binding.playlistRV.setHasFixedSize(true)
                binding.playlistRV.layoutManager = LinearLayoutManager(requireContext())
                val musicAdapter =
                    MusicAdapter(requireContext(), musicPlayerService.musicListMA, this)
                binding.playlistRV.adapter = musicAdapter
            }
        }
    }

    override fun onMusicItemClicked(musicPosition: Int) {
        musicPlayerService.playNewMusic(musicPosition)
    }

    private fun applyMiniPlayer(music: Music) {
        binding.miniPlayerContainer.visibility = View.VISIBLE
        Glide.with(this)
            .load(music.artUri)
            .into(binding.imageMV)
        binding.songNameMV.text = music.title
    }

    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            ?: "Unknown"
                    val idC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                            ?: "Unknown"
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                            ?: "Unknown"
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            ?: "Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return tempList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}