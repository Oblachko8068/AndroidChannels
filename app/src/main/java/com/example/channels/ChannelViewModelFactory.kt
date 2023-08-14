package com.example.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.channels.repository.DownloadRepository

class ChannelViewModelFactory(
    private val downloadRepository: DownloadRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(DownloadRepository::class.java)
            .newInstance(downloadRepository)
    }
}