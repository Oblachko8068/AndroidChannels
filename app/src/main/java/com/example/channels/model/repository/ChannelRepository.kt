package com.example.channels.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.channels.model.retrofit.ChannelDb
import com.example.channels.model.room.ChannelDao
import com.example.channels.model.room.ChannelDbEntity
import com.example.channels.model.room.ViewingTheChannelListTuple
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChannelRepository(
    context: Context,
    private val channelDao: ChannelDao
) {

    private val sharedPreferences = context.getSharedPreferences("SharedPrefsChannel", Context.MODE_PRIVATE)
    val updateChannelLiveData = MutableLiveData<Unit>()

    fun updateChannelList(channelDbList: List<ChannelDb>, channelDbListLiveData: MutableLiveData<List<ChannelDb>>) {
        channelDbListLiveData.value = channelDbList
        //saveChannelListToSharedPref(channelDbList)

        for (channel in channelDbList) {
            createChannel(channel)
        }
        notifyChannelsUpdated()
    }

    /*fun getChannelListLiveData(): LiveData<List<ChannelDb>> {
       val json = sharedPreferences.getString("channel_list", null)
        val savedList = if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<ChannelDb>>() {}.type
            Gson().fromJson<List<ChannelDb>>(json, type)
        } else {
            emptyList()
        }
        val liveData = MutableLiveData<List<ChannelDb>>()
        liveData.value = savedList
        return liveData
    }*/

    private fun saveChannelListToSharedPref(channelDbList: List<ChannelDb>) {
        val json = Gson().toJson(channelDbList)
        sharedPreferences.edit().putString("channel_list", json).apply()
    }
    ////////////////////попытка уведомить вьюмодел
    private fun notifyChannelsUpdated() {
        updateChannelLiveData.postValue(Unit)
    }

    fun getUpdateChannelLiveData(): LiveData<Unit> {
        return updateChannelLiveData
    }

    ///////////////рум
   suspend fun findViewingTheChannelList(): ViewingTheChannelListTuple? { //получает каналы без stream
        return channelDao.findViewingTheChannelListTuple()
    }

    fun getChannelList(): List<ChannelDb?> { //получает каналы полностью
        return channelDao.getChannelListAll().map { ChannelDbEntity -> ChannelDbEntity?.toChannelDb()}
    }

    fun createChannel(channelDb: ChannelDb) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = ChannelDbEntity.fromChannelDb(channelDb)
            channelDao.createChannel(entity)
        }
    }

    fun getChannelListLiveData(): LiveData<List<ChannelDb>> {
        val liveData = MutableLiveData<List<ChannelDb>>()

        CoroutineScope(Dispatchers.IO).launch {
            val channelList = channelDao.getChannelListAll().mapNotNull { ChannelDbEntity -> ChannelDbEntity?.toChannelDb() }

            if (channelList.isEmpty()) {
                println("Список каналов пуст")
            }

            liveData.postValue(channelList)
        }

        return liveData
    }

}



