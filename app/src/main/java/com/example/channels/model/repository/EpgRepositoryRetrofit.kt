package com.example.channels.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.channels.model.retrofit.Epg
import com.example.channels.model.room.EpgDao
import com.example.channels.model.room.EpgDbEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpgRepositoryRetrofit(
    private val epgDao: EpgDao
) : EpgRepository  {

    override fun updateEpgList(epgDbEntityList: List<EpgDbEntity>) {
        createEpg(epgDbEntityList)
    }
    override fun createEpg(epgDbEntityList: List<EpgDbEntity>){ // создает епг
        CoroutineScope(Dispatchers.IO).launch {
            epgDao.createEpg(epgDbEntityList)
        }
    }
    override fun getEpgListLiveData(): LiveData<List<Epg>> {
        return epgDao.getEpgListAll().map { it.map{ it.toEpgDb()} }
    }

}
