package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.model.Channel

interface ChannelRepository {

    fun getChannelListLiveData(): LiveData<List<Channel>>
}