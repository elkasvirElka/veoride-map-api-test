package com.elviraminnullina.map_api.data.repository

import com.elviraminnullina.map_api.data.model.CoordinationModel
import com.elviraminnullina.map_api.data.model.DirectionResponce
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
    ): Response<DirectionResponce> {
        val origin = "${start.lat}, ${start.lng}"
        val destination = "${finish.lat}, ${finish.lng}"
        return withContext(Dispatchers.IO) {
            directionService.direction(origin, destination, mode)
        }
    }

}