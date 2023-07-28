package com.example.channels

import android.content.Context

data class Channels(
    var name: String,
    var description: String,
    var iconResource: Int,
    var icon_fav: Int,
    var fav_selected: Boolean
) {

    constructor(name: String, description: String, iconResource: Int) : this(name, description, iconResource, R.drawable.baseline_star_24, false)

    companion object {
        private val allChannelsList = mutableListOf<Channels>()

        fun getCatList(context: Context): List<Channels> {
            if (allChannelsList.isEmpty()) {
                val catNames = context.resources.getStringArray(R.array.channel_names)
                val catDescriptions = context.resources.getStringArray(R.array.channel_desc)
                val catIcons = context.resources.obtainTypedArray(R.array.channel_icons)

                for (i in catNames.indices) {
                    val name = catNames[i]
                    val description = catDescriptions[i]
                    val iconResource = catIcons.getResourceId(i, R.drawable.ic_launcher_background)
                    val channel = Channels(name, description, iconResource)
                    allChannelsList.add(channel)
                }
                catIcons.recycle()
            }
            return allChannelsList
        }
    }
}