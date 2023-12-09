package com.example.data.model

import retrofit2.Call
import retrofit2.http.*
//Вынести в папку network или remote. Это же не модель данных
interface ChannelsApi {
    @GET("rKkyTS")
    fun getChannelList(): Call<ChannelsJson> //Возвращаем Response<ChannelsJson> network или remote. Это же не модель данных

}