package com.example.channels

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.channels.databinding.ChannelPlayerBinding
import com.example.channels.retrofit.ChannelDB
import com.example.channels.retrofit.EpgDB
import com.squareup.picasso.Picasso

class ChannelPlayer : AppCompatActivity() {

    private lateinit var binding: ChannelPlayerBinding
    private var channelStream =
        "https://ia804503.us.archive.org/15/items/kikTXNL6MvX6ZpRXM/kikTXNL6MvX6ZpRXM.mp4"
    private var currentVideoPosition = 0
    private val handler = Handler()

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChannelPlayerBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        showOtherViews()

        binding.container.setOnClickListener {
            showOtherViews()
        }
        val bundle = intent.extras
        val channelDB = bundle?.getSerializable("channel_data") as? ChannelDB
        val epgDB = bundle?.getSerializable("epg_data") as? EpgDB
        if (channelDB != null) {
            // Извлекаем данные из Bundle
            val channelName = channelDB.name
            val channelDescription = epgDB?.title
            val channelIconResource = channelDB.image
            //val channelStream = extras.getString("channel_stream")
            val channelTimestart = epgDB?.timestart
            val channelTimestop = epgDB?.timestop

            //запись имени
            binding.activeChannelName.text = channelName

            //запись описания
            binding.activeChannelDesc.text = "$channelDescription"

            //запись иконки
            Picasso.get()
                .load(channelIconResource)
                .into(binding.activeChannelIcon)

            //запись видео
            val channelStreamUri = Uri.parse(channelStream)
            binding.playerVideoView.setVideoURI(channelStreamUri)

            binding.playerVideoView.setOnPreparedListener {
                // Запуск воспроизведения после подготовки видео
                it.start()
            }

            binding.playerVideoView.setOnCompletionListener {
                // Вы можете добавить действия по завершению воспроизведения здесь
            }

            // время до окончания

            if (channelTimestop != null) {
                updateRemainingTime(channelTimestop)
            }

            //устанавливаем полоску
            //val totalTime = channelTimestop - channelTimestart // Общая продолжительность передачи в секундах
            val totalTime = 60
            val channelTimestart1 = System.currentTimeMillis() / 1000
            updateProgressBar(totalTime, channelTimestart1)
        }

        //кнопка назад
        binding.backToMain.setOnClickListener {
            onBackPressed()
        }

        // Сохраняем текущую позицию видео при его завершении
        binding.playerVideoView.setOnCompletionListener {
            currentVideoPosition = 0
        }

        // Назначьте обработчик нажатия на кнопку "настройки"
        binding.settings.setOnClickListener {
            // Создайте объект класса PopupMenu, указав контекст и вью для привязки
            val popupMenu = PopupMenu(this, binding.settings)

            // Загрузите ресурс с всплывающим меню
            popupMenu.menuInflater.inflate(R.menu.menu_settings, popupMenu.menu)
            // Установите обработчик нажатия на элементы меню
            popupMenu.setOnMenuItemClickListener { item ->
                // Сохраняем текущую позицию видео при смене качества
                currentVideoPosition = binding.playerVideoView.currentPosition

                channelStream = when (item.itemId) {
                    R.id.action_setting1 -> channelStream
                    R.id.action_setting2 -> channelStream
                    R.id.action_setting3 -> channelStream
                    // Добавьте обработку других пунктов меню для других качеств видео
                    else -> channelStream // По умолчанию оставляем текущий URL
                }
                updateVideoView()
                true
            }
            popupMenu.show()
        }

    }

    /////////////////////////////////////////////////////////////////////////////
    private fun hideOtherViews() {
        binding.layoutTop.visibility = View.INVISIBLE
        binding.layoutBottom.visibility = View.INVISIBLE
        binding.backToMain.visibility = View.INVISIBLE
        binding.activeChannelIcon.visibility = View.INVISIBLE
        binding.activeChannelDesc.visibility = View.INVISIBLE
        binding.activeChannelName.visibility = View.INVISIBLE
    }

    private fun showOtherViews() {
        binding.layoutTop.visibility = View.VISIBLE
        binding.layoutBottom.visibility = View.VISIBLE
        binding.backToMain.visibility = View.VISIBLE
        binding.activeChannelIcon.visibility = View.VISIBLE
        binding.activeChannelDesc.visibility = View.VISIBLE
        binding.activeChannelName.visibility = View.VISIBLE
        Handler().postDelayed({
            hideOtherViews()
        }, 3000)
    }

    private fun updateVideoView() {
        val channelStreamUri = Uri.parse(channelStream)
        binding.playerVideoView.setVideoURI(channelStreamUri)

        binding.playerVideoView.setOnPreparedListener { mediaPlayer ->
            // Восстанавливаем позицию видео перед запуском
            mediaPlayer.seekTo(currentVideoPosition)
            mediaPlayer.start()
        }

        binding.playerVideoView.setOnCompletionListener {

        }
    }

    //устанавливаем время до окончания
    private fun updateRemainingTime(channelTimestop: Long) {
        val currentTime = System.currentTimeMillis() / 1000 // Текущее время в секундах
        val timeTTE = (channelTimestop - currentTime) / 60 // Время до конца в минутах

        // Обновляем текст в TextView
        binding.textViewTimeToTheEnd.text = "Осталось $timeTTE минут"

        // Запускаем обновление каждую минуту (или секунду, если нужно)
        handler.postDelayed(
            { updateRemainingTime(channelTimestop) },
            10 * 1000
        ) // 60 * 1000 = 1 минута
    }

    // Функция для обновления прогресса полоски
    private fun updateProgressBar(totalTime: Int, channelTimestart1: Long) {
        val interval = 1000L // Интервал обновления прогресса в миллисекундах (10 секунд)
        val currentTime = System.currentTimeMillis() / 1000 // Текущее время в секундах
        val elapsedTime =
            currentTime - channelTimestart1 // Время, которое пользователь уже смотрит передачу

        // Вычисляем прогресс в процентах
        val progress = (elapsedTime.toFloat() / totalTime.toFloat()) * 100

        // Устанавливаем ширину полоски в процентах
        binding.progressBar.layoutParams.width =
            (progress * resources.displayMetrics.density).toInt()
        binding.progressBar.requestLayout()

        // Повторяем обновление прогресса через заданный интервал
        handler.postDelayed({ updateProgressBar(totalTime, channelTimestart1) }, interval)
    }
}