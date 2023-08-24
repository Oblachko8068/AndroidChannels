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

/*TODO 1. исправить кнопку настройки( меню показывается снизу)
       2. добавить обработку proggressbar
       3. показ на весь экран
 */

class VideoPlayerFragment : Fragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var channelStream =
        "https://ia804503.us.archive.org/15/items/kikTXNL6MvX6ZpRXM/kikTXNL6MvX6ZpRXM.mp4"
    private var currentVideoPosition = 0
    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!
    private var visibilityView: Boolean = true

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
       // _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channel = arguments?.getSerializable("channel_data") as? Channel
        val epg = arguments?.getSerializable("epg_data") as? Epg
        if (channel != null) {
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
            val totalTime = (channelTimestop?.minus(channelTimestart!!))?.div(60)
            val channelTimestart1 = System.currentTimeMillis() / 1000
            binding.textViewTimeToTheEnd.text = totalTime.toString()
            //updateProgressBar(totalTime, channelTimestart1)
        }
        //кнопка назад
        binding.backToMain.setOnClickListener {
            val fragment =
                requireActivity().supportFragmentManager.findFragmentById(android.R.id.content)!!
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
        binding.container.setOnClickListener {
            if (binding.playerVideoView.isPlaying) {
                coroutineScope.launch {
                    if (visibilityView) {
                        hideOtherViews()
                        visibilityView = false
                    } else {
                        showOtherViews()
                        delay(3000)
                        hideOtherViews()
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
                when (item.itemId) {
                    R.id.action_setting1 -> {
                        true
                    }

                    R.id.action_setting2 -> {
                        true
                    }

                    R.id.action_setting3 -> {
                        true
                    }

                    R.id.action_setting4 -> {
                        true
                    }

                    R.id.action_setting5 -> {
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
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
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.window?.decorView?.systemUiVisibility = 0
        coroutineScope.cancel()
    }
}

