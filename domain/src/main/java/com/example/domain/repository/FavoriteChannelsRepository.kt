package com.example.domain.repository

import com.example.domain.model.Channel


interface FavoriteChannelsRepository {

    fun isChannelFavorite(channelId: Int): Boolean

    fun getSavedFavChannelsArray(): IntArray

    fun addOrRemoveChannelFromFavoriteChannels(channel: Channel)
}