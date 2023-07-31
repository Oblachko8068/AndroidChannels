package com.example.channels

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.os.Handler
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout

class ChannelPlayer : AppCompatActivity() {
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_player)


        hideOtherViews()
        val showButton = findViewById<ConstraintLayout>(R.id.container)
        showButton.setOnClickListener {
            showOtherViews()
        }
        val extras = intent.extras

        if (extras != null) {
            // Извлекаем данные из Bundle
            val channelName = extras.getString("channel_name")
            val channelDescription = extras.getString("channel_description")
            val channelIconResource = extras.getInt("channel_icon_resource")

            //запись имени
            val chName = findViewById<TextView>(R.id.activeChannelName)
            chName.text = "$channelName"

            //запись описания
            val chDesc = findViewById<TextView>(R.id.activeChannelDesc)
            chDesc.text = "$channelDescription"

            //запись иконки
            val chIcon = findViewById<ImageView>(R.id.activeChannelIcon)
            chIcon.setImageResource(channelIconResource)

            //установка бэка
            val container = findViewById<ConstraintLayout>(R.id.container)
            val channelNamesArray = resources.getStringArray(R.array.channel_names)
            // В зависимости от канала устанавливаем разные background
            for (i in channelNamesArray.indices) {
                if (channelNamesArray[i] == channelName) {
                    // Нашли соответствующий канал, устанавливаем background
                    val resourceId = getResourceIdByName("background_channel_$i", "drawable")
                    container.setBackgroundResource(resourceId)
                    break
                }
            }
        }

        //кнопка назад
        val backButton = findViewById<ImageButton>(R.id.backToMain)
        backButton.setOnClickListener {
            onBackPressed()
        }

        ////////////кнопка выбора качества
        val settingsButton: View = findViewById(R.id.settings)

        // Назначьте обработчик нажатия на кнопку "настройки"
        settingsButton.setOnClickListener {
            // Создайте объект класса PopupMenu, указав контекст и вью для привязки
            val popupMenu = PopupMenu(this, settingsButton)

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

    }
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
        }, 3000)
    }
    private fun getResourceIdByName(name: String, type: String): Int {
        return resources.getIdentifier(name, type, packageName)
    }

}