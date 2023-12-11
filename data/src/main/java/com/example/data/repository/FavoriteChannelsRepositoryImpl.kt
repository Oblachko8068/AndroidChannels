package com.example.data.repository

import android.content.Context
import com.example.domain.model.Channel
import com.example.domain.repository.FavoriteChannelsRepository
import com.example.domain.repository.SharedPrefRepository
import javax.inject.Inject

class FavoriteChannelsRepositoryImpl @Inject constructor(
    context: Context,
    private val sharedPrefRepository : SharedPrefRepository
) : FavoriteChannelsRepository {

    private val sharedPref =
        context.getSharedPreferences("fav_channels_preferences", Context.MODE_PRIVATE)

    override fun isChannelFavorite(channelId: Int): Boolean {
        val favChannelsArray = getSavedFavChannelsArray()
        return channelId in favChannelsArray
    }

    override fun addOrRemoveChannelFromFavoriteChannels(channel: Channel) {
        var favChannelsArray = getSavedFavChannelsArray()
        if (isChannelFavorite(channel.id)) {
            for (i in favChannelsArray.indices) {
                if (favChannelsArray[i] == channel.id) {
                    favChannelsArray = removeElementFromArray(favChannelsArray, i)
                    break
                }
            }
            saveNewFavChannelsArray(favChannelsArray)
        } else {
            favChannelsArray = addElementToArray(favChannelsArray, channel.id)
            saveNewFavChannelsArray(favChannelsArray)
        }
    }

    private fun addElementToArray(array: IntArray, element: Int): IntArray {
        val newFavChannelsArray = IntArray(array.size + 1)
        array.copyInto(newFavChannelsArray)
        newFavChannelsArray[array.size] = element
        return newFavChannelsArray
    }

    private fun removeElementFromArray(array: IntArray, indexToRemove: Int): IntArray {
        return array.filterIndexed { index, _ -> index != indexToRemove }.toIntArray()
    }

    override fun getSavedFavChannelsArray(): IntArray =
        sharedPrefRepository.getSavedIntArray(sharedPref)

    private fun saveNewFavChannelsArray(intArray: IntArray) {
        sharedPrefRepository.saveNewIntArray(sharedPref, intArray)
    }
}
