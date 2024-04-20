package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.model.Radio

interface RadioRepository {

    fun getRadioListLiveData(): LiveData<List<Radio>>
}