package com.example.channels.exoPlayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PiPModeActionsReceiver : BroadcastReceiver() {

    companion object {
        private lateinit var pipModeActionsListener: PiPModeActionsListener
        private const val EXTRA_CONTROL_TYPE = "control_type"
        private const val REQUEST_PLAY = 1
        private const val REQUEST_PAUSE = 2

        fun createIntent(context: Context, requestPlay: Boolean): Intent{
            val intent = Intent(context, PiPModeActionsReceiver::class.java)
            intent.putExtra(EXTRA_CONTROL_TYPE, if (requestPlay) REQUEST_PAUSE else REQUEST_PLAY)
            return intent
        }

        fun setListener(listener: PiPModeActionsListener) {
            pipModeActionsListener = listener
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
            REQUEST_PAUSE -> pipModeActionsListener.onPauseClick()
            REQUEST_PLAY -> pipModeActionsListener.onPlayClick()
        }
    }
}