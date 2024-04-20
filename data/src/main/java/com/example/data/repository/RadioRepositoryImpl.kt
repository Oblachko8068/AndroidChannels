package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.data.room.RadioDao
import com.example.domain.model.Radio
import com.example.domain.repository.RadioRepository
import javax.inject.Inject

class RadioRepositoryImpl @Inject constructor(
    private val radioDao: RadioDao
) : RadioRepository {

    override fun getRadioListLiveData(): LiveData<List<Radio>> = radioDao.getRadioListAll()
        .map { radioDbEntityList -> radioDbEntityList.map { it.toRadioDb() } }
}