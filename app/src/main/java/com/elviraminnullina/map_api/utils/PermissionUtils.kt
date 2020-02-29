package com.elviraminnullina.map_api.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.elviraminnullina.map_api.Constants.Companion.MY_PERMISSIONS_REQUEST_LOCATION
import com.elviraminnullina.map_api.ui.map.MapFragment

class PermissionUtils {
    companion object {
        fun checkLocationPermission(activity: Activity, context: Context): Boolean {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
                return false
            } else {
                return true
            }
        }
    }
}