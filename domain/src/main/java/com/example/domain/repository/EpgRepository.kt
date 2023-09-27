package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.model.Epg

interface EpgRepository {
    fun getEpgListLiveData(): LiveData<List<Epg>>
    fun getCurrentEpgByChannelId(channelID: Int): LiveData<Epg>

}