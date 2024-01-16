package com.example.channels.fragments

import androidx.fragment.app.Fragment
import com.example.domain.model.Channel
import com.example.domain.model.Epg

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun showVideoPlayerFragment(channel: Channel, selectedEpgDb: Epg?)

    fun goBack()

}