package com.example.data.repository

import com.example.data.model.fromRadioToRadioDbEntity
import com.example.data.room.RadioDao
import com.example.domain.model.Radio
import com.example.domain.repository.RadioDownloadRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RadioDownloadRepositoryImpl @Inject constructor(
    private val radioDao: RadioDao,
    private val databaseReference: DatabaseReference
): RadioDownloadRepository {

    override suspend fun fetchRadio(){
        val value = databaseReference.child("radios")
        value.addValueEventListener (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val typeIndicator = object : GenericTypeIndicator<List<Radio>>() {}
                    val radios = snapshot.getValue(typeIndicator)
                    if (radios != null){
                        CoroutineScope(Dispatchers.IO).launch {
                            radioDao.createRadio(radios.map { it.fromRadioToRadioDbEntity() })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}