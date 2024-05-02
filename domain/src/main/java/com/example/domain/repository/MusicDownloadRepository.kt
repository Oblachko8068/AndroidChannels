package com.example.domain.repository

interface MusicDownloadRepository {

    suspend fun fetchMusic()
}