package com.elviraminnullina.map_api.framework

import com.elviraminnullina.map_api.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiFactory {
    companion object {
        private lateinit var sRetrofit: Retrofit

        lateinit var httpClient: OkHttpClient

        fun getRetrofitInstance() = sRetrofit

        private fun provideClient() =  httpClient
    }
}