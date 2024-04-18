package com.example.channels.exoPlayer

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.example.channels.R

@RequiresApi(Build.VERSION_CODES.O)
object PipManager {

    private val paramsBuilder = PictureInPictureParams.Builder()
    private lateinit var exoPlayer: ExoPlayerFragment
    private lateinit var context: FragmentActivity

    fun enterPipMode(fragmentActivity: FragmentActivity?, exoPlayerFragment: ExoPlayerFragment) {
        exoPlayer = exoPlayerFragment
        fragmentActivity?.let { context = it }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && context.packageManager?.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) == true
        ) {
            context.enterPictureInPictureMode(getPipBuilder(true).build())
        }
    }

    fun setPipPauseParams() {
        context.setPictureInPictureParams(getPipBuilder(true).build())
    }

    fun setPipPlayParams() {
        context.setPictureInPictureParams(getPipBuilder(false).build())
    }

    private fun getPipBuilder(toPlay: Boolean): PictureInPictureParams.Builder {
        PiPModeActionsReceiver.setListener(exoPlayer)
        val icon = Icon.createWithResource(
            context,
            if (toPlay) R.drawable.radio_pause_button else R.drawable.radio_play_button
        )
        val intent = PiPModeActionsReceiver.createIntent(context as Context, toPlay)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            if (toPlay) 2 else 1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val title = if (toPlay) "Pause" else "Play"
        val action = RemoteAction(icon, title, "$title Video", pendingIntent)
        return paramsBuilder.setActions(listOf(action))
    }
}