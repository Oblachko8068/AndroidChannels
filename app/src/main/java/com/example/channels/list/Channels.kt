package com.example.channels.list

import android.content.Context
import com.example.channels.R
import com.google.gson.Gson

data class Channels(
    var name: String,
    var description: String,
    var iconResource: Int,
    var icon_fav: Int,
    var fav_selected: Boolean
) {
    constructor(name: String, description: String, iconResource: Int) : this(name, description, iconResource, R.drawable.baseline_star_24, false)

    companion object {
        fun getCatList(context: Context): List<Channels> {
            val catNames = context.resources.getStringArray(R.array.channel_names)
            val catDescriptions = context.resources.getStringArray(R.array.channel_desc)
            val catIcons = context.resources.obtainTypedArray(R.array.channel_icons)
            val intArray = getSavedIntArrayOrFallback(context) // Получаем массив целых чисел из SharedPreferences или ресурсов
            val channelList = ArrayList<Channels>()

            for (i in catNames.indices) {
                val name = catNames[i]
                val description = catDescriptions[i]
                val iconResource = catIcons.getResourceId(i, R.drawable.ic_launcher_background)

                // Проверяем значение элемента массива и устанавливаем fav_selected в зависимости от него
                val favSelected = intArray[i] == 1

                val channel = Channels(name, description, iconResource, R.drawable.baseline_star_24, favSelected)
                channelList.add(channel)
            }

            catIcons.recycle()

            return channelList
        }

        // Добавляем функцию для получения массива целочисленных значений из SharedPreferences или ресурсов
        fun getSavedIntArrayOrFallback(context: Context): IntArray {
            val sharedPref = context.getSharedPreferences("array_preferences", Context.MODE_PRIVATE)
            val jsonString = sharedPref.getString("int_array_data", null)

            return if (jsonString != null) {
                try {
                    Gson().fromJson(jsonString, IntArray::class.java)
                } catch (e: Exception) {
                    context.resources.getIntArray(R.array.default_int_array)
                }
            } else {
                context.resources.getIntArray(R.array.default_int_array)
            }
        }

        // Добавляем функцию для сохранения массива целочисленных значений в SharedPreferences
        fun saveIntArray(context: Context, intArray: IntArray) {
            val sharedPref = context.getSharedPreferences("array_preferences", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val jsonString = Gson().toJson(intArray)
            editor.putString("int_array_data", jsonString)
            editor.apply()
        }
    }
}
