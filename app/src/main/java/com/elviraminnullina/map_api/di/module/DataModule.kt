package com.elviraminnullina.map_api.di.module

import com.elviraminnullina.map_api.data.repository.MapRepository
import com.elviraminnullina.map_api.data.repository.MapRepositoryImpl
import com.elviraminnullina.map_api.service.MapService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun mapRepository(service: MapService): MapRepository =
        MapRepositoryImpl(service)
}
