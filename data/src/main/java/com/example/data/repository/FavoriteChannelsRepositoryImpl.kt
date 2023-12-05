package com.example.data.repository

import android.content.Context
import com.example.domain.repository.FavoriteChannelsRepository
import javax.inject.Inject

class FavoriteChannelsRepositoryImpl @Inject constructor(
    context: Context
) :
    FavoriteChannelsRepository {

    private val sharedPrefRepository = SharedPrefRepositoryImpl()
    private val sharedPref =
        context.getSharedPreferences("fav_channels_preferences", Context.MODE_PRIVATE)

    override fun isChannelFavorite(channelId: Int): Boolean {
        var favChannelsArray = getSavedNewFavChannelsArray()
        if (channelId in favChannelsArray) {
            for (i in favChannelsArray.indices) {
                if (favChannelsArray[i] == channelId) {
                    favChannelsArray = removeElementFromArray(favChannelsArray, i)
                    break
                }
            }
            saveNewFavChannelsArray(favChannelsArray)
            return true
        } else {
            favChannelsArray = addElementToArray(favChannelsArray, channelId)
            saveNewFavChannelsArray(favChannelsArray)
            return false
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

    override fun getSavedNewFavChannelsArray(): IntArray =
        sharedPrefRepository.getSavedNewIntArray(sharedPref)

    private fun saveNewFavChannelsArray(intArray: IntArray) {
        sharedPrefRepository.saveNewIntArray(sharedPref, intArray)
    }
}
