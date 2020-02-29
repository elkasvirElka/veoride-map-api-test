package com.elviraminnullina.map_api.service

import com.elviraminnullina.map_api.data.model.DirectionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {

    @GET("json")
    suspend fun direction(
        @Query("origin") origin: String = "",
        @Query("destination") destination: String = "",
        @Query ("mode") mode:String? = null) : Response<DirectionResponse>
}