package com.example.channels

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChannelPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_player)
        val channelName = intent.getStringExtra("channel_name")

        // Используем полученное название канала для отображения соответствующего окна
        val channelWindow = findViewById<TextView>(R.id.channelWindow)
        channelWindow.text = "Вы смотрите канал $channelName"
    }
}