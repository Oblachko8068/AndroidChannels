package com.example.channels.model.repository

import androidx.lifecycle.LiveData
import com.example.channels.model.retrofit.Epg
import com.example.channels.model.room.EpgDbEntity

interface EpgRepository {

    fun getEpgListLiveData(): LiveData<List<Epg>>

    fun updateEpgList(epgDbEntityList: List<EpgDbEntity>)

    fun createEpg(epgDbEntityList: List<EpgDbEntity>)

    fun getCurrentEpgByChannelId(channelID: Int): LiveData<Epg>
}