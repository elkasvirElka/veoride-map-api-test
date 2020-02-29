package com.elviraminnullina.map_api

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elviraminnullina.map_api.service.location_service.LocationService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = MyApplication.getInstance()?.getDatabase()
        db?.coordinationDataBase()?.deleteAll()
    }

    fun startService(){
        startService(Intent(this, LocationService::class.java))
    }
    fun stopService(){
        stopService(Intent(this, LocationService::class.java))
    }
}
