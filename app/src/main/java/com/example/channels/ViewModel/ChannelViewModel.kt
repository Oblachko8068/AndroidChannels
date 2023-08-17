package com.example.channels.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.channels.model.repository.ChannelRepository
import com.example.channels.model.repository.DownloadRepository
import com.example.channels.model.repository.EpgRepository
import com.example.channels.model.retrofit.ChannelDb
import com.example.channels.model.retrofit.EpgDb


class ChannelViewModel(
    private val downloadRepository: DownloadRepository,
    private val channelRepository: ChannelRepository,
    private val epgRepository: EpgRepository,
) : ViewModel() {

    private val updateChannelLiveData = channelRepository.getUpdateChannelLiveData()
    private val updateEpgLiveData = epgRepository.getUpdateEpgLiveData()

    private var channelDbLiveData: LiveData<List<ChannelDb>> = channelRepository.getChannelListLiveData()
    private var epgDbLiveData: LiveData<List<EpgDb>> = epgRepository.getEpgListLiveData()

    /*private val channelDbLiveData: LiveData<List<ChannelDb>> = Transformations.switchMap(updateChannelLiveData) {
        channelRepository.getChannelListLiveData()
    }
    private val epgDbLiveData: LiveData<List<EpgDb>> = Transformations.switchMap(updateEpgLiveData) {
        epgRepository.getEpgListLiveData()
    }*/
    init {
        channelRepository.updateChannelLiveData.observeForever {
            channelDbLiveData = Transformations.switchMap(updateChannelLiveData) {
                channelRepository.getChannelListLiveData()
            }
            epgDbLiveData = Transformations.switchMap(updateEpgLiveData) {
                epgRepository.getEpgListLiveData()
            }
        }

        epgRepository.updateEpgLiveData.observeForever {
            channelDbLiveData = Transformations.switchMap(updateChannelLiveData) {
                channelRepository.getChannelListLiveData()
            }
            epgDbLiveData = Transformations.switchMap(updateEpgLiveData) {
                epgRepository.getEpgListLiveData()
            }
        }

        updateChannelLiveData.observeForever {
            channelDbLiveData = Transformations.switchMap(updateChannelLiveData) {
                channelRepository.getChannelListLiveData()
            }
            epgDbLiveData = Transformations.switchMap(updateEpgLiveData) {
                epgRepository.getEpgListLiveData()
            }
        }
        updateEpgLiveData.observeForever {
            channelDbLiveData = Transformations.switchMap(updateChannelLiveData) {
                channelRepository.getChannelListLiveData()
            }
            epgDbLiveData = Transformations.switchMap(updateEpgLiveData) {
                epgRepository.getEpgListLiveData()
            }
        }
    }

    fun fetchChannels() {
        downloadRepository.fetchChannels()
    }

    fun getChannelListLiveData(): LiveData<List<ChannelDb>> {
        return channelDbLiveData
    }

    fun getEpgListLiveData(): LiveData<List<EpgDb>>{
        return epgDbLiveData
    }

}

