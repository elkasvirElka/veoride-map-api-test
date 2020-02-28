package com.elviraminnullina.map_api.di

import com.elviraminnullina.map_api.data.repository.MapRepositoryImpl
import com.elviraminnullina.map_api.framework.ApiFactory
import com.elviraminnullina.map_api.service.MapService

object InjectorUtils {

    fun provideRepositoryViewModel() =
        MapRepositoryImpl(ApiFactory.getRetrofitInstance().create((MapService::class.java)))

}