package com.example.channels.fragments

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.channels.R
import com.example.channels.databinding.FragmentVideoPlayerBinding
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.Epg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable

class VideoPlayerFragment : Fragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var channelStream =
        "https://cdn-cache01.voka.tv/live/5117.m3u8"
    private var currentVideoPosition = 0
    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!
    private var visibilityView: Boolean = true

    private lateinit var channel: Channel
    private lateinit var epg: Epg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*channel = arguments?.getSerializable<Channel>(channel_data)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
        visibilityView = true
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channel = arguments?.getSerializable("channel_data") as? Channel
        val epg = arguments?.getSerializable("epg_data") as? Epg
        if (channel != null ) {
            val channelName = channel.name
            val channelDescription = epg?.title
            val channelIconResource = channel.image
            //val channelStream = extras.getString("channel_stream")
            val channelTimestart = epg?.timestart
            val channelTimestop = epg?.timestop
            binding.activeChannelName.text = channelName

            //запись описания
            binding.activeChannelDesc.text = "$channelDescription"

            //запись иконки
            context?.let {
                Glide.with(it)
                    .load(channelIconResource)
                    .into(binding.activeChannelIcon)
            }

            //запись видео
            binding.playerVideoView.setVideoURI(Uri.parse(channelStream))
            binding.playerVideoView.setOnPreparedListener {
                it.start()
            }



            //устанавливаем полоску
            //val totalTime = channelTimestop - channelTimestart // Общая продолжительность передачи в секундах
            //val totalTime = (channelTimestop?.minus(channelTimestart!!))?.div(60)
            val totalTime = 60
            val channelTimestart1 = System.currentTimeMillis() / 1000

            updateProgressBar(totalTime, channelTimestart1)

            //время до конца
            binding.textViewTimeToTheEnd.text = totalTime.toString()
        }
        //кнопка назад
        binding.backToMain.setOnClickListener {
            navigator().goBack()
        }
        binding.container.setOnClickListener {
            if (binding.playerVideoView.isPlaying) {
                coroutineScope.launch {
                    if (visibilityView) {
                        hideSystemUI()
                        hideOtherViews()
                        visibilityView = false
                    } else {
                        showOtherViews()
                        delay(3000)
                        hideOtherViews()
                        hideSystemUI()
                    }
                }
            }
        }
        // Сохраняем текущую позицию видео при его завершении
        binding.playerVideoView.setOnCompletionListener {
            currentVideoPosition = 0
        }

        binding.settings.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.settings)
            popupMenu.menuInflater.inflate(R.menu.menu_settings, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                currentVideoPosition = binding.playerVideoView.currentPosition

                channelStream = when (item.itemId) {
                    R.id.action_setting1 -> channelStream
                    R.id.action_setting2 -> channelStream
                    R.id.action_setting3 -> channelStream
                    // Добавьте обработку других пунктов меню для других качеств видео
                    else -> channelStream
                }
                updateVideoView()
                true
                }
            popupMenu.show()
        }
    }

    private fun updateVideoView() {
        val videoView = binding.playerVideoView
        val channelStreamUri = Uri.parse(channelStream)
        videoView.setVideoURI(channelStreamUri)

        videoView.setOnPreparedListener { mediaPlayer ->
            // Восстанавливаем позицию видео перед запуском
            mediaPlayer.seekTo(currentVideoPosition)
            mediaPlayer.start()
        }

        videoView.setOnCompletionListener {

        }
    }

    private fun showOtherViews() {
        binding.layoutTop.visibility = View.VISIBLE
        binding.layoutBottom.visibility = View.VISIBLE
        binding.backToMain.visibility = View.VISIBLE
        binding.activeChannelIcon.visibility = View.VISIBLE
        binding.activeChannelDesc.visibility = View.VISIBLE
        binding.activeChannelName.visibility = View.VISIBLE
    }


    private fun hideOtherViews() {
        binding.layoutTop.visibility = View.INVISIBLE
        binding.layoutBottom.visibility = View.INVISIBLE
        binding.backToMain.visibility = View.INVISIBLE
        binding.activeChannelIcon.visibility = View.INVISIBLE
        binding.activeChannelDesc.visibility = View.INVISIBLE
        binding.activeChannelName.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        //activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        super.onPause()
        //activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.window?.decorView?.systemUiVisibility = 0
        coroutineScope.cancel()
    }

    private fun updateProgressBar(totalTime: Int, channelTimestart1: Long) {
        val interval = 1000L // Интервал обновления прогресса в миллисекундах (1 секунда)

        coroutineScope.launch(Dispatchers.Main) {
            while (true) {
                val currentTime = System.currentTimeMillis() / 1000 // Текущее время в секундах
                val elapsedTime = currentTime - channelTimestart1 // Время, которое пользователь уже смотрит передачу

                // Вычисляем прогресс в процентах
                val progress = (elapsedTime.toFloat() / totalTime.toFloat()) * 100

                // Устанавливаем ширину полоски в процентах
                binding.progressBar.layoutParams.width = (progress * resources.displayMetrics.density).toInt()
                binding.progressBar.requestLayout()

                delay(interval)
            }
        }
    }

    private fun hideSystemUI() {
        val decorView: View = requireActivity().window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
    }

    private fun showSystemUI() {
        val decorView: View = requireActivity().window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
        decorView.systemUiVisibility = newUiOptions
    }

     companion object {
         @JvmStatic private val channel_data = "channel_data"
         @JvmStatic private val epg_data = "epg_data"
        @JvmStatic
        fun newInstance(channel: Channel, selectedEpgDb: Epg?): VideoPlayerFragment {
            val args = Bundle()
            args.putSerializable(channel_data, channel)
            args.putSerializable(epg_data, selectedEpgDb)
            val fragment = VideoPlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }

}

