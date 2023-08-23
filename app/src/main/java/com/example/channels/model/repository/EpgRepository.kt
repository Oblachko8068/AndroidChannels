package com.example.channels.model.repository

import androidx.lifecycle.LiveData
import com.example.channels.model.retrofit.EpgDb

interface EpgRepository {

    fun getEpgListLiveData(): LiveData<List<EpgDb>>

    fun updateEpgList(epgDbList: List<EpgDb>)

    fun createEpg(epgDb: List<EpgDb>)
}