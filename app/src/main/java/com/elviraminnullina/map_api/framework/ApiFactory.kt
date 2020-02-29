package com.elviraminnullina.map_api.framework

import okhttp3.OkHttpClient
import retrofit2.Retrofit

class ApiFactory {
    companion object {
        private lateinit var sRetrofit: Retrofit

        private lateinit var httpClient: OkHttpClient

        fun getRetrofitInstance() = sRetrofit

        private fun provideClient() = httpClient
    }
}