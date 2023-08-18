package com.example.channels.fragments

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.channels.R
import com.example.channels.databinding.FragmentVideoPlayerBinding
import com.example.channels.model.retrofit.ChannelDb
import com.example.channels.model.retrofit.EpgDb
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*TODO 1. исправить кнопку настройки( меню показывается снизу)
       2. добавить обработку proggressbar
 */

class VideoPlayerFragment : Fragment() {

    private val hideHandler = Handler(Looper.myLooper()!!)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        val flags =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        fullscreenContentControls?.visibility = View.VISIBLE
    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }

    private var channelStream =
        "https://ia804503.us.archive.org/15/items/kikTXNL6MvX6ZpRXM/kikTXNL6MvX6ZpRXM.mp4"
    private var currentVideoPosition = 0
    private val handler = Handler()


    private var fullscreenContent: View? = null
    private var fullscreenContentControls: View? = null

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visible = true
        hideOtherViews()

        val channelDb = arguments?.getSerializable("channel_data") as? ChannelDb
        val epgDb = arguments?.getSerializable("epg_data") as? EpgDb
        if (channelDb != null) {
            val channelName = channelDb.name
            val channelDescription = epgDb?.title
            val channelIconResource = channelDb.image
            //val channelStream = extras.getString("channel_stream")
            val channelTimestart = epgDb?.timestart
            val channelTimestop = epgDb?.timestop
            binding.activeChannelName.text = channelName

            //запись описания
            binding.activeChannelDesc.text = "$channelDescription"

            //запись иконки
            Picasso.get()
                .load(channelIconResource)
                .into(binding.activeChannelIcon)

            //запись видео
            //val channelStreamUri = Uri.parse(channelStream)
            binding.playerVideoView.setVideoURI(Uri.parse(channelStream))

            binding.playerVideoView.setOnPreparedListener {
                // Запуск воспроизведения после подготовки видео
                it.start()
            }
            binding.container.setOnClickListener {
                coroutineScope.launch {
                    showOtherViews()
                    delay(3000)
                    hideOtherViews()
                }
            }
            //устанавливаем полоску
            //val totalTime = channelTimestop - channelTimestart // Общая продолжительность передачи в секундах
            val totalTime = (channelTimestop?.minus(channelTimestart!!))?.div(60)
            val channelTimestart1 = System.currentTimeMillis() / 1000
            binding.textViewTimeToTheEnd.text = totalTime.toString()
            //updateProgressBar(totalTime, channelTimestart1)
        }
        //кнопка назад
        binding.backToMain.setOnClickListener{
            val fragment = requireActivity().supportFragmentManager.findFragmentById(android.R.id.content)!!
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commit()
            //activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        // Сохраняем текущую позицию видео при его завершении
        binding.playerVideoView.setOnCompletionListener {
            currentVideoPosition = 0
        }
        // Назначьте обработчик нажатия на кнопку "настройки"
        binding.settings.setOnClickListener {
            // Создайте объект класса PopupMenu, указав контекст и вью для привязки
            val popupMenu = PopupMenu(requireContext(), binding.settings)
            // Загрузите ресурс с всплывающим меню
            popupMenu.menuInflater.inflate(R.menu.menu_settings, popupMenu.menu)
            // Установите обработчик нажатия на элементы меню
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
        //полноэкранный режим
        fullscreenContent?.setOnClickListener { toggle() }
    }
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel all coroutines when the view is destroyed
        _binding = null
    }
    private fun showOtherViews() {
        binding.layoutTop.visibility = View.VISIBLE
        binding.layoutBottom.visibility = View.VISIBLE
        binding.backToMain.visibility = View.VISIBLE
        binding.activeChannelIcon.visibility = View.VISIBLE
        binding.activeChannelDesc.visibility = View.VISIBLE
        binding.activeChannelName.visibility = View.VISIBLE
    }

    private fun hideOtherViews(){
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
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        fullscreenContentControls?.visibility = View.GONE
        visible = false
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        fullscreenContent?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        visible = true
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }


    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        private const val UI_ANIMATION_DELAY = 300
    }
}

