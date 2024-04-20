package com.example.channels

import androidx.fragment.app.Fragment
import com.example.domain.model.Channel
import com.example.domain.model.Epg

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun showLoginFragment()

    fun showChannelFragment()

    fun showRadioFragment()

    fun showSettingsFragment()

    fun showMusicFragment()

    fun showVideoPlayerFragment(channel: Channel, selectedEpgDb: Epg?)

    fun goBack()

}