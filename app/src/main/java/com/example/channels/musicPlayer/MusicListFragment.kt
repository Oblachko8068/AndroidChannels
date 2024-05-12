package com.example.channels.musicPlayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.Player
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.channels.R
import com.example.channels.databinding.FragmentMusicListBinding
import com.example.channels.viewModels.MusicViewModel
import com.example.channels.viewModels.MusicViewModel.Companion.searchTextLiveData
import com.example.channels.viewModels.MusicViewModel.Companion.setSearchText
import com.example.domain.model.Music
import kotlin.concurrent.thread

class MusicListFragment : Fragment(), MusicAdapter.OnMusicItemClickListener {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicPlayerService: MusicPlayerService
    private lateinit var connection: ServiceConnection
    private var isBound = false
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val musicViewModel: MusicViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        initServiceConnection()
        registerPermissionListener()
        checkAudioPermission()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyMusicSeekBarListener()
        binding.miniPlayerContainer.visibility = View.GONE
        binding.startStopMusic.setOnClickListener {
            togglePlayer()
        }
        binding.nextMusic.setOnClickListener {
            if (isBound) musicPlayerService.playNext()
        }
        binding.prevMusic.setOnClickListener {
            if (isBound) musicPlayerService.playPrevious()
        }
    }

    private fun initServiceConnection() {
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicPlayerService.MusicBinder
                musicPlayerService = binder.service
                isBound = true
                musicPlayerService.currentMusicPositionLiveData.observe(viewLifecycleOwner) {
                    applyMiniPlayer(musicPlayerService.getCurrentMusic())
                }
                musicPlayerService.musicListMA = musicViewModel.getMusicList()
                musicPlayerService.setSeekBar(binding.musicSeekBar)
                musicPlayerService.musicPlayer.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            updateSeekBarPosition()
                        }
                    }
                })
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
            }
        }
        val serviceIntent = Intent(requireActivity(), MusicPlayerService::class.java)
        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun togglePlayer() {
        if (isBound) {
            if (musicPlayerService.isPlaying) {
                musicPlayerService.pausePlayer()
                binding.startStopMusic.setImageResource(R.drawable.music_play)
            } else {
                musicPlayerService.startPlayer()
                binding.startStopMusic.setImageResource(R.drawable.music_pause)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {
        thread {
            if (isBound) {
                musicPlayerService.musicListMA = musicViewModel.getMusicList()
            }
            requireActivity().runOnUiThread {
                val recyclerView = binding.playlistRV
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = MusicAdapter(requireContext(), musicViewModel.getMusicList(), this)
                val adapter = recyclerView.adapter as MusicAdapter
                val musicLiveData = musicViewModel.getMusicLiveData()
                musicLiveData.observe(viewLifecycleOwner) {
                    if (isBound) {
                        musicPlayerService.musicListMA = it
                    }
                    adapter.updateData(it)
                }
                binding.searchView.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false

                    override fun onQueryTextChange(newText: String?): Boolean {
                        setSearchText(newText.orEmpty())
                        return true
                    }
                })
                searchTextLiveData.observe(viewLifecycleOwner) {
                    adapter.updateData(musicViewModel.getFilteredMusic())
                }
            }
        }
    }

    override fun onMusicItemClicked(musicPosition: Int) {
        if (isBound) {
            musicPlayerService.playNewMusic(musicPosition)
        }
    }

    private fun applyMiniPlayer(music: Music) {
        binding.miniPlayerContainer.visibility = View.VISIBLE
        Glide.with(this)
            .load(music.artUri)
            .transform(RoundedCorners(18))
            .into(binding.imageMV)
        binding.songNameMV.text = music.title
        binding.musicSeekBar.max = music.duration.toInt()
    }

    private fun applyMusicSeekBarListener() {
        binding.musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicPlayerService.musicPlayer.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                musicPlayerService.pausePlayer()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                musicPlayerService.startPlayer()
            }
        })
    }

    private fun updateSeekBarPosition() {
        binding.musicSeekBar.progress = musicPlayerService.musicPlayer.currentPosition.toInt()
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (musicPlayerService.musicPlayer.isPlaying) {
                updateSeekBarPosition()
            }
        }, 500)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkAudioPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            // Версия Android 12 (API Level 31) и выше
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    initializeLayout()
                }
                else -> {
                    pLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }
        } else {
            // Версия Android ниже 12
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    initializeLayout()
                }
                else -> {
                    pLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun registerPermissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                initializeLayout()
            } else {
                Toast.makeText(requireContext(), "Необходимо разрешение", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unbindService(connection)
        _binding = null
    }
}