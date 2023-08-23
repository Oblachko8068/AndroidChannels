package com.example.channels.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.channels.model.retrofit.ChannelDb
import com.example.channels.model.retrofit.EpgDb
import com.example.channels.model.room.EpgDao
import com.example.channels.model.room.EpgDbEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class EpgRepository(
    context: Context,
    private val epgDao: EpgDao
)  {

    private val sharedPreferences = context.getSharedPreferences("SharedPrefsEpg", Context.MODE_PRIVATE)
    var updateEpgLiveData = MutableLiveData<Unit>()

    fun updateEpgList(epgDbList: List<EpgDb>, epgDbListLiveData: MutableLiveData<List<EpgDb>>) {
        epgDbListLiveData.value = epgDbList
        //saveEpgListToSharedPref(epgDbList)

        for (epg in epgDbList) {
            createEpg(epg)
        }
        notifyEpgUpdated()
    }

    /*fun getEpgListLiveData(): LiveData<List<EpgDb>> {
        val json = sharedPreferences.getString("epg_list", null)
        val savedList = if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<EpgDb>>() {}.type
            Gson().fromJson<List<EpgDb>>(json, type)
        } else {
            emptyList()
        }
        val liveData = MutableLiveData<List<EpgDb>>()
        liveData.value = savedList
        return liveData
    }*/

    private fun saveEpgListToSharedPref(epgDbList: List<EpgDb>) {
        val json = Gson().toJson(epgDbList)
        sharedPreferences.edit().putString("epg_list", json).apply()
    }
    /////////////попыька уведомить вью модел
    fun notifyEpgUpdated() {
        updateEpgLiveData.postValue(Unit)
    }

    fun getUpdateEpgLiveData(): LiveData<Unit> {
        return updateEpgLiveData
    }

    /////////////рум
    fun getEpgList(): List<EpgDb?> {   //////////получение списка епг целиком
        return epgDao.getEpgListAll().map {EpgDbEntity -> EpgDbEntity?.toEpgDb() }
    }

    fun createEpg(epgDb: EpgDb){ // создает епг
        CoroutineScope(Dispatchers.IO).launch {
            val entity = EpgDbEntity.fromEpgDb(epgDb)
            epgDao.createEpg(entity)
        }
    }

    /*fun getEpgListLiveData(): LiveData<List<EpgDb>> {
        val liveData = MutableLiveData<List<EpgDb>>()

        CoroutineScope(Dispatchers.IO).launch {
            val epgList = epgDao.getEpgListAll().mapNotNull {EpgDbEntity -> EpgDbEntity?.toEpgDb() }

            if (epgList.isEmpty()) {
                println("Список каналов пуст")
            }


            liveData.postValue(epgList)
        }

        return liveData
    }*/

    fun getEpgListLiveData(): LiveData<List<EpgDb>> {
        return epgDao.getEpgListAllTestVersion().map { EpgDbEntity -> EpgDbEntity.map { EpgDbEntity -> EpgDbEntity.toEpgDb() } }
    }

}
