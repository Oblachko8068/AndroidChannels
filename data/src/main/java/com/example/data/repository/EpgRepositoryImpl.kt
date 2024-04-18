package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.data.room.EpgDao
import com.example.domain.model.Epg
import javax.inject.Inject

class EpgRepositoryImpl @Inject constructor(
    private val epgDao: EpgDao
) : com.example.domain.repository.EpgRepository {

    override fun getEpgListLiveData(): LiveData<List<Epg>> =
        epgDao.getEpgListAll().map { epgDbEntityList -> epgDbEntityList.map { it.toEpgDb() } }

    override fun getCurrentEpgByChannelId(channelID: Int): LiveData<Epg> =
        epgDao.getEpgByChannelID(channelID).map { it.toEpgDb() }
}
