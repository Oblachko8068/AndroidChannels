package com.example.data.network

import com.example.data.model.RadiosJson
import retrofit2.Response
import retrofit2.http.GET

interface RadioApi {

    @GET("radios")
    suspend fun getRadioList(): Response<RadiosJson>

}