package com.example.data.repository

import com.example.data.model.ChannelJson
import com.example.data.model.EpgDbEntity
import com.example.data.model.fromChannelJsonToChannelDbEntity
import com.example.data.model.fromChannelJsonToEpgDbEntity
import com.example.data.model.fromRadioToRadioDbEntity
import com.example.data.network.ChannelsApi
import com.example.data.room.ChannelDao
import com.example.data.room.EpgDao
import com.example.domain.model.Channel
import com.example.domain.model.Radio
import com.example.domain.repository.DownloadRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class DownloadRepositoryRetrofit @Inject constructor(
    private val retrofit: Retrofit,
    private val channelDao: ChannelDao,
    private val epgDao: EpgDao
) : DownloadRepository {

    override suspend fun fetchChannels() {
        /*val response = retrofit
            .create(ChannelsApi::class.java)
            .getChannelList()
        if (response.isSuccessful) {
            if (response.body() != null) {
                val channelList = response.body()?.channels ?: emptyList()
                val channelDbEntityList =
                    channelList.map { it.fromChannelJsonToChannelDbEntity() }
                val epgDBEntityList: ArrayList<EpgDbEntity> = arrayListOf()
                channelList.forEach { epgDBEntityList.addAll(it.fromChannelJsonToEpgDbEntity()) }
                CoroutineScope(Dispatchers.IO).launch {
                    channelDao.createChannel(channelDbEntityList)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    epgDao.createEpg(epgDBEntityList)
                }
            }
        }*/

        var DATABASE = FirebaseDatabase.getInstance("https://channels-41585-default-rtdb.europe-west1.firebasedatabase.app/")
        var DB_REF = DATABASE.getReference()
        var channelDat = DB_REF.child("channels")
        channelDat.addValueEventListener (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val typeIndicator = object : GenericTypeIndicator<List<ChannelJson>>() {}
                    val channelList = snapshot.getValue(typeIndicator)
                    if (channelList != null){

                        val channelDbEntityList =
                            channelList.map { it.fromChannelJsonToChannelDbEntity() }
                        val epgDBEntityList: ArrayList<EpgDbEntity> = arrayListOf()
                        channelList.forEach { epgDBEntityList.addAll(it.fromChannelJsonToEpgDbEntity()) }
                        CoroutineScope(Dispatchers.IO).launch {
                            channelDao.createChannel(channelDbEntityList)
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            epgDao.createEpg(epgDBEntityList)
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}