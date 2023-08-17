package com.example.channels.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.channels.model.retrofit.ChannelDb
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChannelRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("SharedPrefsChannel", Context.MODE_PRIVATE)
    val updateChannelLiveData = MutableLiveData<Unit>()

    fun updateChannelList(channelDbList: List<ChannelDb>, channelDbListLiveData: MutableLiveData<List<ChannelDb>>) {
        channelDbListLiveData.value = channelDbList
        saveChannelListToSharedPref(channelDbList)
        notifyChannelsUpdated()
    }

    fun getChannelListLiveData(): LiveData<List<ChannelDb>> {
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
    }

    private fun saveChannelListToSharedPref(channelDbList: List<ChannelDb>) {
        val json = Gson().toJson(channelDbList)
        sharedPreferences.edit().putString("channel_list", json).apply()
    }
    ////////////////////
    private fun notifyChannelsUpdated() {
        updateChannelLiveData.postValue(Unit)
    }

    fun getUpdateChannelLiveData(): LiveData<Unit> {
        return updateChannelLiveData
    }

}



