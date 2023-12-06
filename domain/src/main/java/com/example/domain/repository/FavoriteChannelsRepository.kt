package com.example.domain.repository


interface FavoriteChannelsRepository {

    fun isChannelFavorite(channelId: Int): Boolean
    fun getSavedFavChannelsArray(): IntArray
}