package com.example.channels.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.channels.model.retrofit.EpgDb
import com.example.channels.model.room.EpgDao
import com.example.channels.model.room.fromEpgDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpgRepositoryRetrofit(
    context: Context,
    private val epgDao: EpgDao
) : EpgRepository  {

    override fun updateEpgList(epgDbList: List<EpgDb>) {
        createEpg(epgDbList)
        /*for (epg in epgDbList) {
            createEpg(epg)
        }*/
    }
    override fun createEpg(epgDb: List<EpgDb>){ // создает епг
        CoroutineScope(Dispatchers.IO).launch {
            val entity = epgDb.map { it.fromEpgDb() }
            epgDao.createEpg(entity)
        }
    }
    override fun getEpgListLiveData(): LiveData<List<EpgDb>> {
        return epgDao.getEpgListAll()
        //return epgDao.getEpgListAll().map { EpgDbEntity -> EpgDbEntity.map { EpgDbEntity -> EpgDbEntity.toEpgDb() } }
    }

}
