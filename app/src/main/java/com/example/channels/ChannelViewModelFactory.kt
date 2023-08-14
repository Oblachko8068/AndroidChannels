package com.example.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.channels.repository.ChannelRepository

class ChannelViewModelFactory(
    private val channelRepository: ChannelRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ChannelRepository::class.java)
            .newInstance(channelRepository)
    }
}