package com.example.domain.repository

import com.example.domain.model.Channel

interface FavoriteChannelsRepository {

    fun getSavedFavChannelsArray(): IntArray

    fun addOrRemoveChannelFromFavoriteChannels(channel: Channel)

    fun isChannelFavorite(channelId: Int): Boolean
}