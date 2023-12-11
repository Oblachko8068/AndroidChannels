package com.example.domain.repository

import android.content.SharedPreferences

interface SharedPrefRepository {

    fun getSavedIntArray(sharedPref: SharedPreferences): IntArray

    fun saveNewIntArray(sharedPref: SharedPreferences, intArray: IntArray)
}