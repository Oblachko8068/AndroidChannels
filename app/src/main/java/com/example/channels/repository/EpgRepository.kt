package com.example.channels.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.channels.retrofit.EpgDb
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EpgRepository(context: Context)  {
    private val sharedPreferences = context.getSharedPreferences("SharedPrefsEpg", Context.MODE_PRIVATE)

    fun updateEpgList(epgDbList: List<EpgDb>, channelDbListLiveData: MutableLiveData<List<EpgDb>>) {
        channelDbListLiveData.value = epgDbList
        saveEpgListToSharedPref(epgDbList)
    }

    fun getEpgListLiveData(): LiveData<List<EpgDb>> {
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
    }

    private fun saveEpgListToSharedPref(channelJsonList: List<EpgDb>) {
        val json = Gson().toJson(channelJsonList)
        sharedPreferences.edit().putString("epg_list", json).apply()
    }
}