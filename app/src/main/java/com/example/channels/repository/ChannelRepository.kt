package com.example.channels.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.channels.retrofit.ChannelDb
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChannelRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("SharedPrefsChannel", Context.MODE_PRIVATE)

    fun updateChannelList(channelDbList: List<ChannelDb>, channelDbListLiveData: MutableLiveData<List<ChannelDb>>) {
        channelDbListLiveData.value = channelDbList
        saveChannelListToSharedPref(channelDbList)
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

    private fun saveChannelListToSharedPref(channelJsonList: List<ChannelDb>) {
        val json = Gson().toJson(channelJsonList)
        sharedPreferences.edit().putString("channel_list", json).apply()
    }
}

