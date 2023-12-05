package com.example.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson

const val FAV_CHANNELS_SHARED_PREF_KEY = "new_int_array_data"

class SharedPrefRepositoryImpl {

    fun getSavedNewIntArray(sharedPref: SharedPreferences): IntArray {
        val jsonString = sharedPref.getString(FAV_CHANNELS_SHARED_PREF_KEY, null)
        return try {
            Gson().fromJson(jsonString, IntArray::class.java) ?: IntArray(0)
        } catch (e: Exception) {
            IntArray(0)
        }
    }

    fun saveNewIntArray(sharedPref: SharedPreferences, intArray: IntArray) {
        val editor = sharedPref.edit()
        val jsonString = Gson().toJson(intArray)
        editor.putString(FAV_CHANNELS_SHARED_PREF_KEY, jsonString)
        editor.apply()
    }
}
