package com.example.domain.repository


interface FavoriteChannelsRepository {

    fun isChannelFavorite(channelId: Int): Boolean
    fun getSavedNewFavChannelsArray(): IntArray
}