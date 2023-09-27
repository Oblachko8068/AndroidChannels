package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.data.room.EpgDao
import com.example.domain.model.Epg

class EpgRepositoryImpl(
    private val epgDao: EpgDao
) : com.example.domain.repository.EpgRepository {

    override fun getEpgListLiveData(): LiveData<List<Epg>> {
        return epgDao.getEpgListAll().map { it.map { it.toEpgDb() } }
    }

    override fun getCurrentEpgByChannelId(channelID: Int): LiveData<Epg> {
        return epgDao.getEpgByChannelID(channelID).map { it.toEpgDb() }
    }

}
