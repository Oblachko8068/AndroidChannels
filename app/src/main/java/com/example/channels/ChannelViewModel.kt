package com.example.channels

import retrofit2.Call
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.channels.retrofit.Channel
import com.example.channels.repository.ChannelRepository
import com.example.channels.retrofit.Channels
import retrofit2.Response
import retrofit2.Callback

class ChannelViewModel : ViewModel() {

    private val channelRepository = ChannelRepository()
    private val channelListLiveData: LiveData<List<Channel>> = channelRepository.getChannelListLiveData()

    fun getChannels() {
        channelRepository.getChannels(object : Callback<Channels> {
            override fun onFailure(call: Call<Channels>, t: Throwable) {
                // Обработка ошибки, если не удалось получить данные
            }

            override fun onResponse(call: Call<Channels>, response: Response<Channels>) {
                val channelList = response.body()?.channels ?: emptyList()
                channelRepository.updateChannelList(channelList)
            }
        })
    }

    fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelListLiveData
    }
}
