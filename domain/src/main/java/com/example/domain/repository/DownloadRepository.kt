package com.example.domain.repository

interface DownloadRepository {

    suspend fun fetchChannels()
}