package com.example.channels

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso

class ChannelPlayer : AppCompatActivity() {

    private var channelStream =
        "https://ia804503.us.archive.org/15/items/kikTXNL6MvX6ZpRXM/kikTXNL6MvX6ZpRXM.mp4"
    private var currentVideoPosition = 0
    private val handler = Handler()

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_player)

        val videoView = findViewById<VideoView>(R.id.playerVideoView)
        val chName = findViewById<TextView>(R.id.activeChannelName)
        val chDesc = findViewById<TextView>(R.id.activeChannelDesc)
        val chIcon = findViewById<ImageView>(R.id.activeChannelIcon)
        val showButton = findViewById<ConstraintLayout>(R.id.container)
        val backButton = findViewById<ImageButton>(R.id.backToMain)
        val settingsButton: View = findViewById(R.id.settings)

        hideOtherViews()

        showButton.setOnClickListener {
            showOtherViews()
        }
        val extras = intent.extras

        if (extras != null) {
            // Извлекаем данные из Bundle
            val channelName = extras.getString("channel_name")
            val channelDescription = extras.getString("channel_description")
            val channelIconResource = extras.getString("channel_icon_resource")
            //val channelStream = extras.getString("channel_stream")
            val channelTimestart = extras.getLong("channel_timestart")
            val channelTimestop = extras.getLong("channel_timestop")

            //запись имени
            chName.text = "$channelName"

            //запись описания
            chDesc.text = "$channelDescription"

            //запись иконки
            Picasso.get()
                .load(channelIconResource)
                .into(chIcon)

            //запись видео
            val channelStreamUri = Uri.parse(channelStream)
            videoView.setVideoURI(channelStreamUri)

            videoView.setOnPreparedListener {
                // Запуск воспроизведения после подготовки видео
                it.start()
            }

            videoView.setOnCompletionListener {
                // Вы можете добавить действия по завершению воспроизведения здесь
            }

            // время до окончания

            updateRemainingTime(channelTimestop)

            //устанавливаем полоску
            //val totalTime = channelTimestop - channelTimestart // Общая продолжительность передачи в секундах
            val totalTime = 60
            val channelTimestart1 = System.currentTimeMillis() / 1000
            updateProgressBar(totalTime, channelTimestart1)
        }

        //кнопка назад
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Сохраняем текущую позицию видео при его завершении
        videoView.setOnCompletionListener {
            currentVideoPosition = 0
        }

        // Назначьте обработчик нажатия на кнопку "настройки"
        settingsButton.setOnClickListener {
            // Создайте объект класса PopupMenu, указав контекст и вью для привязки
            val popupMenu = PopupMenu(this, settingsButton)

            // Загрузите ресурс с всплывающим меню
            popupMenu.menuInflater.inflate(R.menu.menu_settings, popupMenu.menu)
            // Установите обработчик нажатия на элементы меню
            popupMenu.setOnMenuItemClickListener { item ->
                // Сохраняем текущую позицию видео при смене качества
                currentVideoPosition = videoView.currentPosition

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
        val layoutTop = findViewById<View>(R.id.layoutTop)
        val layoutBottom = findViewById<View>(R.id.layoutBottom)
        val backButton = findViewById<View>(R.id.backToMain)
        val activeChannelIcon = findViewById<View>(R.id.activeChannelIcon)
        val activeChannelDesc = findViewById<View>(R.id.activeChannelDesc)
        val activeChannelName = findViewById<View>(R.id.activeChannelName)
        layoutTop.visibility = View.INVISIBLE
        layoutBottom.visibility = View.INVISIBLE
        backButton.visibility = View.INVISIBLE
        activeChannelIcon.visibility = View.INVISIBLE
        activeChannelDesc.visibility = View.INVISIBLE
        activeChannelName.visibility = View.INVISIBLE
    }

    private fun showOtherViews() {
        val layoutTop = findViewById<View>(R.id.layoutTop)
        val layoutBottom = findViewById<View>(R.id.layoutBottom)
        val backButton = findViewById<View>(R.id.backToMain)
        val activeChannelIcon = findViewById<View>(R.id.activeChannelIcon)
        val activeChannelDesc = findViewById<View>(R.id.activeChannelDesc)
        val activeChannelName = findViewById<View>(R.id.activeChannelName)
        layoutTop.visibility = View.VISIBLE
        layoutBottom.visibility = View.VISIBLE
        backButton.visibility = View.VISIBLE
        activeChannelIcon.visibility = View.VISIBLE
        activeChannelDesc.visibility = View.VISIBLE
        activeChannelName.visibility = View.VISIBLE
        Handler().postDelayed({
            hideOtherViews()
        }, 10000)
    }

    private fun updateVideoView() {
        val videoView = findViewById<VideoView>(R.id.playerVideoView)
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

    //устанавливаем время до окончания
    private fun updateRemainingTime(channelTimestop: Long) {
        val chTimeTTE = findViewById<TextView>(R.id.textViewTimeToTheEnd)
        val currentTime = System.currentTimeMillis() / 1000 // Текущее время в секундах
        val timeTTE = (channelTimestop - currentTime) / 60 // Время до конца в минутах

        // Обновляем текст в TextView
        chTimeTTE.text = "Осталось $timeTTE минут"

        // Запускаем обновление каждую минуту (или секунду, если нужно)
        handler.postDelayed(
            { updateRemainingTime(channelTimestop) },
            10 * 1000
        ) // 60 * 1000 = 1 минута
    }

    // Функция для обновления прогресса полоски
    private fun updateProgressBar(totalTime: Int, channelTimestart1: Long) {
        val progressBar = findViewById<View>(R.id.progressBar)
        val interval = 1000L // Интервал обновления прогресса в миллисекундах (10 секунд)
        val currentTime = System.currentTimeMillis() / 1000 // Текущее время в секундах
        val elapsedTime =
            currentTime - channelTimestart1 // Время, которое пользователь уже смотрит передачу

        // Вычисляем прогресс в процентах
        val progress = (elapsedTime.toFloat() / totalTime.toFloat()) * 100

        // Устанавливаем ширину полоски в процентах
        progressBar.layoutParams.width = (progress * resources.displayMetrics.density).toInt()
        progressBar.requestLayout()

        // Повторяем обновление прогресса через заданный интервал
        handler.postDelayed({ updateProgressBar(totalTime, channelTimestart1) }, interval)
    }
}