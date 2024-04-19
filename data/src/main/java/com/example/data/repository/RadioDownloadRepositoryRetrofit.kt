package com.example.data.repository

import com.example.data.model.RadiosJson
import com.example.data.model.fromRadioToRadioDbEntity
import com.example.data.network.RadioApi
import com.example.data.room.RadioDao
import com.example.domain.model.Radio
import com.example.domain.repository.RadioDownloadRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class RadioDownloadRepositoryRetrofit @Inject constructor(
    private val retrofit: Retrofit,
    private val radioDao: RadioDao
): RadioDownloadRepository {

    override suspend fun fetchRadio(){
        /*val response = retrofit
            .create(RadioApi::class.java)
            .getRadioList()
        if(response.isSuccessful){
            if(response.body() != null){
                val radioList = response.body()?.radios ?: emptyList()
                CoroutineScope(Dispatchers.IO).launch {
                    radioDao.createRadio(radioList.map { it.fromRadioToRadioDbEntity() })
                }
            }
        }*/

        var DATABASE = FirebaseDatabase.getInstance("https://channels-41585-default-rtdb.europe-west1.firebasedatabase.app/")
        var DB_REF = DATABASE.getReference()
        var test = DB_REF.child("radios")
        test.addValueEventListener (object : ValueEventListener{
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