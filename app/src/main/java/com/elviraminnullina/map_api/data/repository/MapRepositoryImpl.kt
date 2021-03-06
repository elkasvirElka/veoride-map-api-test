package com.elviraminnullina.map_api.data.repository

import com.elviraminnullina.map_api.data.model.CoordinationModel
import com.elviraminnullina.map_api.data.model.DirectionResponse
import com.elviraminnullina.map_api.service.MapService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(private val directionService: MapService) :
MapRepository {
    override suspend fun direction(
        start: CoordinationModel,
        finish: CoordinationModel,
        mode: String?
    ): Response<DirectionResponse> {
        val origin = "${start.latitude}, ${start.longitude}"
        val destination = "${finish.latitude}, ${finish.longitude}"
        return withContext(Dispatchers.IO) {
            directionService.direction(origin, destination, mode)
        }
    }

}