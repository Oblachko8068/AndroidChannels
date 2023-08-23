package com.example.channels.model.repository

interface DownloadRepository {
    fun fetchChannels()

    fun fetchEpg()
}
