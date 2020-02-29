package com.elviraminnullina.map_api.data.repository

import com.elviraminnullina.map_api.data.model.CoordinationModel
import com.elviraminnullina.map_api.data.model.DirectionResponse
import retrofit2.Response

interface MapRepository {
    suspend fun direction(
        start: CoordinationModel,
        finish: CoordinationModel,
        mode: String? = null
    ): Response<DirectionResponse>
}