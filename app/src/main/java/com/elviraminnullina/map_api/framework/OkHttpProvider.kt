package com.elviraminnullina.map_api.framework

import okhttp3.OkHttpClient

class OkHttpProvider {
    companion object {
        fun provideClient(): OkHttpClient {
            return OkHttpClient.Builder().addInterceptor(ApiKeyInterceptor()).build()
        }
    }
}