package com.example.channels.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.channels.model.retrofit.EpgDb
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EpgRepository(context: Context)  {

    private val sharedPreferences = context.getSharedPreferences("SharedPrefsEpg", Context.MODE_PRIVATE)
    var updateEpgLiveData = MutableLiveData<Unit>()

    fun updateEpgList(epgDbList: List<EpgDb>, epgDbListLiveData: MutableLiveData<List<EpgDb>>) {
        epgDbListLiveData.value = epgDbList
        saveEpgListToSharedPref(epgDbList)
        notifyEpgUpdated()
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

    private fun saveEpgListToSharedPref(epgDbList: List<EpgDb>) {
        val json = Gson().toJson(epgDbList)
        sharedPreferences.edit().putString("epg_list", json).apply()
    }
    /////////////
    fun notifyEpgUpdated() {
        updateEpgLiveData.postValue(Unit)
    }

    fun getUpdateEpgLiveData(): LiveData<Unit> {
        return updateEpgLiveData
    }
}
