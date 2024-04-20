package com.example.data.repository

import com.example.data.model.fromRadioToRadioDbEntity
import com.example.data.room.RadioDao
import com.example.domain.model.Radio
import com.example.domain.repository.RadioDownloadRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RadioDownloadRepositoryFirebaseDatabase @Inject constructor(
    private val radioDao: RadioDao
): RadioDownloadRepository {

    override suspend fun fetchRadio(){

        val database = FirebaseDatabase.getInstance("https://channels-41585-default-rtdb.europe-west1.firebasedatabase.app/")
        val dbRef = database.getReference()
        val value = dbRef.child("radios")
        value.addValueEventListener (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val typeIndicator = object : GenericTypeIndicator<List<Radio>>() {}
                    val radios = snapshot.getValue(typeIndicator)
                    if (radios != null){
                        CoroutineScope(Dispatchers.IO).launch {
                            radioDao.createRadio(radios.map { it.fromRadioToRadioDbEntity() })
                            val p = 0
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}