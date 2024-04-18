package com.example.channels.exoPlayer

import android.app.PictureInPictureParams
import android.os.Build
import androidx.annotation.RequiresApi

object PipManager {

    @RequiresApi(Build.VERSION_CODES.O)
    private val paramsBuilder = PictureInPictureParams.Builder()

}