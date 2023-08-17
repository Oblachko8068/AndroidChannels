package com.example.channels.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.channels.model.repository.ChannelRepository
import com.example.channels.model.repository.DownloadRepository
import com.example.channels.model.repository.EpgRepository

class ChannelViewModelFactory(
    private val downloadRepository: DownloadRepository,
    private val channelRepository: ChannelRepository,
    private val epgRepository: EpgRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChannelViewModel::class.java)) {
            return ChannelViewModel(downloadRepository,channelRepository, epgRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}