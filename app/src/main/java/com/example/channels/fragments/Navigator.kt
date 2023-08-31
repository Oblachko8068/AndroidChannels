package com.example.channels.fragments

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.Epg
import java.io.Serializable

typealias ResultListener<T> = (T) -> Unit

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun showVideoPlayerFragment(channel: Channel, selectedEpgDb: Epg ?)

    fun goBack()

}