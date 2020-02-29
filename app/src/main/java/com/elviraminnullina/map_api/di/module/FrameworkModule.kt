package com.elviraminnullina.map_api.di.module

import com.elviraminnullina.map_api.BuildConfig
import com.elviraminnullina.map_api.framework.OkHttpProvider
import com.elviraminnullina.map_api.service.MapService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class FrameworkModule {

    @Singleton
    @Provides
    fun getRetrofitInstance(): MapService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(provideClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(MapService::class.java)
    }

    private fun provideClient(): OkHttpClient {
        return OkHttpProvider.provideClient()
    }
}