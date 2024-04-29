package com.example.domain.repository

interface ChannelDownloadRepository {

    suspend fun fetchChannels()
}