package com.example.channels.repository

import androidx.lifecycle.MutableLiveData
import com.example.channels.retrofit.ChannelJSON


class ChannelRepository {
    fun updateChannelList(channelJSONList: List<ChannelJSON>, channelJSONListLiveData: MutableLiveData<List<ChannelJSON>>) {
        channelJSONListLiveData.value = channelJSONList
    }
}

