package com.elviraminnullina.map_api

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.elviraminnullina.map_api.component.AppComponent
import com.elviraminnullina.map_api.component.DaggerAppComponent
import com.elviraminnullina.map_api.database.AppDatabase

class MyApplication : Application() {

    private lateinit var appComponent: AppComponent

    companion object {
        private var database: AppDatabase? = null
        private var instance: MyApplication? = null

        fun getApp(context: Context?): MyApplication {
            return context?.applicationContext as MyApplication
        }

        fun getInstance() = instance

    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        appComponent = DaggerAppComponent.builder()
            .build()
        database = Room.databaseBuilder<AppDatabase>(
            this,
            AppDatabase::class.java,
            getString(R.string.database)
        ).allowMainThreadQueries().build()

    }

    fun getDatabase(): AppDatabase? {
        return database
    }

    fun getAppComponent(): AppComponent = appComponent
}